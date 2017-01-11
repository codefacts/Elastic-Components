package elasta.orm.nm.upsert.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.nm.upsert.*;
import elasta.orm.nm.upsert.ex.InvalidValueException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

            final TableData tableData = tableDataPopulator.populate(jsonObject);

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

                List<JsonObject> columnValues = handleIndirectDependency(
                    indirectDependency,
                    tableData
                );

                columnValues.forEach(jo -> tableValues.getMap().putAll(jo.getMap()));
            }

            for (BelongsTo belongsTo : belongsTos) {
                handleBelongTo(belongsTo, tableValues);
            }

            return tableData;
        }

        private void handleBelongTo(BelongsTo belongsTo, JsonObject tableValues) {

            final Object value = jsonObject
                .getValue(
                    belongsTo.getFieldName()
                );

            if (value instanceof Map) {

                handleBelongToJsonObject(belongsTo, tableValues, new JsonObject(castToMap(value)));

            } else if (value instanceof JsonObject) {

                handleBelongToJsonObject(belongsTo, tableValues, (JsonObject) value);

            } else if (value instanceof List) {

                handleBelongToJsonObjectList(belongsTo, tableValues, new JsonArray((List) value));

            } else if (value instanceof JsonArray) {

                handleBelongToJsonObjectList(belongsTo, tableValues, (JsonArray) value);

            } else {
                throw new InvalidValueException("Value should be object or array. Key: '" + belongsTo.getFieldName() + "', Value: " + value);
            }
        }

        private void handleBelongToJsonObjectList(BelongsTo belongsTo, JsonObject tableValues, JsonArray jsonArray) {

            for (int i = 0; i < jsonArray.size(); i++) {
                handleBelongToJsonObject(belongsTo, tableValues, jsonArray.getJsonObject(i));
            }

        }

        private TableData handleBelongToJsonObject(BelongsTo belongsTo, JsonObject tableValues, JsonObject value) {

            return belongsTo
                .getBelongToHandler()
                .pushUpsert(
                    value,
                    belongsTo
                        .getDependencyColumnValuePopulator()
                        .populate(tableValues),
                    upsertContext
                );
        }

        private List<JsonObject> handleIndirectDependency(
            IndirectDependency indirectDependency, TableData tableData) {

            final Object value = jsonObject
                .getValue(
                    indirectDependency.getFieldName()
                );

            if (value instanceof Map) {

                return ImmutableList.of(
                    handleIndirectJsonObject(indirectDependency, tableData, new JsonObject(castToMap(value)))
                );

            } else if (value instanceof JsonObject) {

                return ImmutableList.of(
                    handleIndirectJsonObject(indirectDependency, tableData, (JsonObject) value)
                );

            } else if (value instanceof List) {

                return handleIndirectJsonObjectList(indirectDependency, tableData, new JsonArray((List) value));

            } else if (value instanceof JsonArray) {

                return handleIndirectJsonObjectList(indirectDependency, tableData, (JsonArray) value);

            } else {
                throw new InvalidValueException("Value should be object or array. Key: '" + indirectDependency.getFieldName() + "', Value: " + value);
            }
        }

        private Map<String, Object> castToMap(Object value) {
            return (Map<String, Object>) value;
        }

        private List<JsonObject> handleIndirectJsonObjectList(IndirectDependency indirectDependency, TableData tableData, JsonArray jsonArray) {

            ImmutableList.Builder<JsonObject> listBuilder = ImmutableList.builder();

            for (int i = 0; i < jsonArray.size(); i++) {
                listBuilder.add(
                    handleIndirectJsonObject(
                        indirectDependency,
                        tableData,
                        jsonArray.getJsonObject(i)
                    )
                );
            }

            return listBuilder.build();
        }

        private JsonObject handleIndirectJsonObject(
            IndirectDependency indirectDependency, TableData tableData, JsonObject jsonObject) {
            return indirectDependency
                .getDependencyColumnValuePopulator()
                .populate(
                    indirectDependency
                        .getIndirectDependencyHandler()
                        .requireUpsert(
                            tableData,
                            jsonObject,
                            upsertContext
                        )
                        .getValues()
                );
        }
    }
}
