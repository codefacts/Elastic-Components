package elasta.orm.nm.upsert.impl;

import elasta.orm.nm.upsert.*;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
public class UpsertFunctionImpl implements UpsertFunction {
    final String entity;
    final TableDataPopulator tableDataPopulator;
    final DirectDependency[] directDependencies;
    final IndirectDependency[] indirectDependencies;
    final BelongsTo[] belongsTos;

    public UpsertFunctionImpl(String entity, TableDataPopulator tableDataPopulator, DirectDependency[] directDependencies, IndirectDependency[] indirectDependencies, BelongsTo[] belongsTos) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(tableDataPopulator);
        Objects.requireNonNull(directDependencies);
        Objects.requireNonNull(indirectDependencies);
        Objects.requireNonNull(belongsTos);

        this.entity = entity;
        this.tableDataPopulator = tableDataPopulator;
        this.directDependencies = directDependencies;
        this.indirectDependencies = indirectDependencies;
        this.belongsTos = belongsTos;
    }

    @Override
    public TableData upsert(JsonObject jsonObject, UpsertContext upsertContext) {

        return new Inserter(jsonObject, upsertContext).insert();
    }

    private class Inserter {
        final JsonObject jsonObject;
        final UpsertContext upsertContext;

        private Inserter(JsonObject jsonObject, UpsertContext upsertContext) {
            this.jsonObject = jsonObject;
            this.upsertContext = upsertContext;
        }

        public TableData insert() {

            final TableData tableData = tableDataPopulator.populate(entity, jsonObject);

            final JsonObject tableValues = tableData.getValues();

            for (DirectDependency directDependency : directDependencies) {

                JsonObject dependencyColumnValues = directDependency.getDependencyColumnValuePopulator()
                    .populate(
                        directDependency
                            .getDependencyHandler()
                            .requireUpsert(
                                jsonObject
                                    .getJsonObject(
                                        directDependency.getFieldName()
                                    ),
                                upsertContext
                            )
                            .getValues()
                    );

                tableValues.getMap().putAll(dependencyColumnValues.getMap());
            }

            upsertContext
                .putOrMerge(
                    UpsertUtils.toTableAndPrimaryColumnsKey(
                        tableData.getTable(), tableData.getPrimaryColumns(), tableValues
                    ),
                    tableData
                );

            for (IndirectDependency indirectDependency : indirectDependencies) {
                JsonObject columnValues = indirectDependency
                    .getDependencyColumnValuePopulator()
                    .populate(
                        indirectDependency
                            .getIndirectDependencyHandler()
                            .requireUpsert(
                                tableData,
                                jsonObject
                                    .getJsonObject(
                                        indirectDependency.getFieldName()
                                    ),
                                upsertContext
                            )
                            .getValues()
                    );
                tableValues.getMap().putAll(columnValues.getMap());
            }

            for (BelongsTo belongsTo : belongsTos) {
                belongsTo
                    .getBelongToHandler()
                    .pushUpsert(
                        jsonObject
                            .getJsonObject(
                                belongsTo.getFieldName()
                            ),
                        belongsTo
                            .getDependencyColumnValuePopulator()
                            .populate(tableValues),
                        upsertContext
                    );
            }

            return tableData;
        }
    }
}
