package elasta.orm.upsert.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.delete.impl.JsonDependencyHandler;
import elasta.orm.upsert.*;
import elasta.orm.upsert.ex.InvalidValueException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * Created by Jango on 2017-01-09.
 */
final public class UpsertFunctionImpl implements UpsertFunction {
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
        Objects.requireNonNull(jsonObject);
        Objects.requireNonNull(upsertContext);
        return new Inserter(jsonObject, upsertContext).insert();
    }

    private class Inserter {
        final JsonObject entity;
        final UpsertContext context;

        private Inserter(JsonObject entity, UpsertContext context) {
            this.entity = entity;
            this.context = context;
        }

        TableData insert() {

            final TableData tableData = tableData();

            final JsonObject tableValues = tableData.getValues();

            String tableAndPrimaryColumnsKey = UpsertUtils.toTableAndPrimaryColumnsKey(
                tableData.getTable(), tableData.getPrimaryColumns(), tableValues
            );

            context.putOrMerge(
                tableAndPrimaryColumnsKey,
                tableData
            );

            for (IndirectDependency indirectDependency : indirectDependencies) {

                handleIndirectDependency(
                    indirectDependency,
                    tableData
                );
            }

            for (BelongsTo belongsTo : belongsTos) {
                handleBelongTo(belongsTo, tableValues);
            }

            return context.get(tableAndPrimaryColumnsKey);
        }

        private TableData tableData() {

            TableData tableData = tableDataPopulator.populate(entity);

            JsonObject tableValues = new JsonObject(
                new LinkedHashMap<>(
                    tableData.getValues().getMap()
                )
            );

            for (DirectDependency directDependency : directDependencies) {

                final String field = directDependency.getField();

                JsonObject jsonObject = this.entity.getJsonObject(field);

                if (jsonObject == null) {
                    continue;
                }

                JsonObject dependencyColumnValues = directDependency.getDependencyColumnValuePopulator()
                    .populate(
                        directDependency
                            .getDependencyHandler()
                            .requireUpsert(
                                jsonObject,
                                context
                            )
                            .getValues()
                    );

                tableValues.getMap().putAll(dependencyColumnValues.getMap());
            }

            return new TableData(
                tableData.getTable(),
                tableData.getPrimaryColumns(),
                new JsonObject(ImmutableMap.copyOf(tableValues.getMap()))
            );
        }

        private void handleBelongTo(BelongsTo belongsTo, JsonObject tableValues) {

            final String field = belongsTo.getField();

            final Object value = entity.getValue(field);

            if (value == null) {
                return;
            }

            new JsonDependencyHandler(jsonObject -> handleBelongToJsonObject(belongsTo, tableValues, jsonObject)).handle(value);
        }

        private void handleBelongToJsonObject(BelongsTo belongsTo, JsonObject jsonObject, JsonObject value) {

            belongsTo
                .getBelongToHandler()
                .pushUpsert(
                    value,
                    belongsTo
                        .getDependencyColumnValuePopulator()
                        .populate(jsonObject),
                    context
                );
        }

        private void handleIndirectDependency(
            IndirectDependency indirectDependency, TableData tableData) {

            final String field = indirectDependency.getField();

            final Object value = entity.getValue(field);

            if (value == null) {
                return;
            }

            new JsonDependencyHandler(jsonObject -> handleIndirectJsonObject(indirectDependency, tableData, jsonObject)).handle(value);
        }

        private JsonObject handleIndirectJsonObject(
            IndirectDependency indirectDependency, TableData tableData, JsonObject jsonObject) {

            return indirectDependency
                .getIndirectDependencyHandler()
                .requireUpsert(
                    tableData,
                    jsonObject,
                    context
                )
                .getValues();
        }
    }
}
