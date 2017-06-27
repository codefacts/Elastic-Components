package elasta.orm.upsert.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.upsert.*;
import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * Created by Jango on 2017-01-09.
 */
final public class UpsertFunctionImpl implements UpsertFunction {
    final String entity;
    final String primaryKey;
    final TableDataPopulator tableDataPopulator;
    final DirectDependency[] directDependencies;
    final IndirectDependency[] indirectDependencies;
    final BelongsTo[] belongsTos;
    final IdGenerator idGenerator;

    public UpsertFunctionImpl(String entity, String primaryKey, TableDataPopulator tableDataPopulator, DirectDependency[] directDependencies, IndirectDependency[] indirectDependencies, BelongsTo[] belongsTos, IdGenerator idGenerator) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(primaryKey);
        Objects.requireNonNull(tableDataPopulator);
        Objects.requireNonNull(directDependencies);
        Objects.requireNonNull(indirectDependencies);
        Objects.requireNonNull(belongsTos);
        Objects.requireNonNull(idGenerator);

        this.entity = entity;
        this.primaryKey = primaryKey;
        this.tableDataPopulator = tableDataPopulator;
        this.directDependencies = directDependencies;
        this.indirectDependencies = indirectDependencies;
        this.belongsTos = belongsTos;
        this.idGenerator = idGenerator;
    }

    @Override
    public Promise<TableData> upsert(final JsonObject jsonObject, final UpsertContext upsertContext) {
        Objects.requireNonNull(jsonObject);
        Objects.requireNonNull(upsertContext);

        final Object entityId = jsonObject.getValue(primaryKey);

        if (entityId == null) {

            return generateId(jsonObject)
                .mapP(jso -> new Inserter(jso, upsertContext, true).insert());
        }

        return new Inserter(jsonObject, upsertContext, false).insert();
    }

    private Promise<JsonObject> generateId(JsonObject jsonObject) {

        return nextId(entity).map(newId -> {

            final Map<String, Object> map = jsonObject.getMap();

            if (map.getClass() == HashMap.class || map.getClass() == LinkedHashMap.class) {
                return jsonObject.put(primaryKey, newId);
            }

            return new JsonObject(
                ImmutableMap.<String, Object>builder()
                    .put(primaryKey, newId)
                    .putAll(map)
                    .build()
            );
        });
    }

    private Promise<Object> nextId(String entity) {
        return idGenerator.nextId(entity);
    }

    private class Inserter {
        final JsonObject entity;
        final UpsertContext context;
        final boolean isNew;

        private Inserter(JsonObject entity, UpsertContext context, boolean isNew) {
            this.entity = entity;
            this.context = context;
            this.isNew = isNew;
        }

        Promise<TableData> insert() {

            return tableData()
                .mapP(tableData -> {

                    final String tableAndPrimaryColumnsKey = putInContext(tableData);

                    ImmutableList.Builder<Promise<Void>> builder = ImmutableList.builder();

                    for (IndirectDependency indirectDependency : indirectDependencies) {

                        builder.add(
                            handleIndirectDependency(
                                indirectDependency,
                                tableData
                            )
                        );
                    }

                    for (BelongsTo belongsTo : belongsTos) {
                        builder.add(
                            handleBelongTo(belongsTo, tableData.getValues())
                        );
                    }

                    return Promises.when(builder.build())
                        .map(voids -> context.get(tableAndPrimaryColumnsKey));
                })
                ;
        }

        private String putInContext(TableData tableData) {

            final JsonObject tableValues = tableData.getValues();

            String tableAndPrimaryColumnsKey = UpsertUtils.toTableAndPrimaryColumnsKey(
                tableData.getTable(), tableData.getPrimaryColumns(), tableValues
            );

            context.putOrMerge(
                tableAndPrimaryColumnsKey,
                tableData
            );

            return tableAndPrimaryColumnsKey;
        }

        private Promise<TableData> tableData() {

            TableData tableData = tableDataPopulator.populate(entity).withIsNew(isNew);

            return loopDirectDependencies(

                tableData.getValues().getMap()

            ).map(tableValues -> new TableData(
                tableData.getTable(),
                tableData.getPrimaryColumns(),
                new JsonObject(ImmutableMap.copyOf(tableValues.getMap()))
            ));
        }

        private Promise<JsonObject> loopDirectDependencies(Map<String, Object> tableValues) {

            Map<String, Object> valuesMap = new LinkedHashMap<>(tableValues);

            ImmutableList.Builder<Promise<Map<String, Object>>> builder = ImmutableList.builder();

            for (DirectDependency directDependency : directDependencies) {

                final String field = directDependency.getField();

                JsonObject jsonObject = this.entity.getJsonObject(field);

                if (jsonObject == null) {
                    continue;
                }

                Promise<Map<String, Object>> promise = directDependency
                    .getDependencyHandler()
                    .requireUpsert(
                        jsonObject,
                        context
                    )
                    .map(tableData -> {

                        JsonObject dependencyColumnValues = directDependency.getDependencyColumnValuePopulator()
                            .populate(tableData.getValues());

                        return dependencyColumnValues.getMap();
                    });

                builder.add(promise);
            }

            return Promises.when(
                builder.build()
            ).map(maps -> {
                maps.forEach(valuesMap::putAll);
                return new JsonObject(valuesMap);
            });
        }

        private Promise<Void> handleBelongTo(BelongsTo belongsTo, JsonObject tableValues) {

            final String field = belongsTo.getField();

            final Object value = entity.getValue(field);

            if (value == null) {
                return Promises.empty();
            }

            return new AsyncJsonDependencyHandler<Void>(jsonObject -> handleBelongToJsonObject(belongsTo, tableValues, jsonObject)).handle(value).map(voids -> null);
        }

        private Promise<Void> handleBelongToJsonObject(BelongsTo belongsTo, JsonObject jsonObject, JsonObject value) {

            return belongsTo
                .getBelongToHandler()
                .pushUpsert(
                    value,
                    belongsTo
                        .getDependencyColumnValuePopulator()
                        .populate(jsonObject),
                    context
                ).map(tableData -> null);
        }

        private Promise<Void> handleIndirectDependency(
            IndirectDependency indirectDependency, TableData tableData) {

            final String field = indirectDependency.getField();

            final Object value = entity.getValue(field);

            if (value == null) {
                return Promises.empty();
            }

            return new AsyncJsonDependencyHandler<Void>(jsonObject -> handleIndirectJsonObject(indirectDependency, tableData, jsonObject)).handle(value).map(voids -> null);
        }

        private Promise<Void> handleIndirectJsonObject(
            IndirectDependency indirectDependency, TableData tableData, JsonObject jsonObject) {

            return indirectDependency
                .getIndirectDependencyHandler()
                .requireUpsert(
                    tableData,
                    jsonObject,
                    context
                )
                .map(data -> null);
        }
    }
}
