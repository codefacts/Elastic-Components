package elasta.composer.module_exporter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.ComposerUtils;
import elasta.core.eventbus.SimpleEventBus;
import elasta.Orm;
import elasta.sql.core.FieldInfo;
import elasta.sql.core.FieldInfoBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Jango on 11/13/2016.
 */
public interface DbEventHandlers {
    static void registerHandlers(SimpleEventBus eventBus, Orm orm) {

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_CREATE,
            (jsonObject, event, params) ->
                orm.insertOrUpdate(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.DATA)
                )
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_UPDATE,
            (jsonObject, event, params) ->
                orm.insertOrUpdate(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.DATA)
                )
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_DELETE,
            (jsonObject, event, params) ->
                orm.delete(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getValue(DbReqParams.ID))
                    .map(id -> jsonObject)
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_FIND,
            (jsonObject, event, params) ->
                orm.findOne(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getValue(DbReqParams.ID), convertToFields(
                        jsonObject
                            .getJsonArray(DbReqParams.PROJECTION, ComposerUtils.emptyJsonArray()).getList()
                    ))
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_FIND_BY_PARAMS,
            (jsonObject, event, params) ->
                orm.findOne(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.CRITERIA),
                    convertToFields(
                        jsonObject
                            .getJsonArray(DbReqParams.PROJECTION, ComposerUtils.emptyJsonArray()).getList()
                    ))
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_FIND_ALL,
            (jsonObject, event, params) ->
                orm.findAll(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.CRITERIA),
                    convertToFields(
                        jsonObject
                            .getJsonArray(DbReqParams.PROJECTION, ComposerUtils.emptyJsonArray()).getList()
                    ))
                    .map(jsonObjects -> new JsonObject(ImmutableMap.of(DbReqParams.DATA, jsonObjects)))
        );
    }

    static List<FieldInfo> convertToFields(List<? extends Object> list) {
        ImmutableList.Builder<FieldInfo> listBuilder = ImmutableList.builder();

        list.stream().map(o -> {

            if (o instanceof Map) {
                return new JsonObject((Map) o);
            }

            return (JsonObject) o;

        }).forEach(new Consumer<JsonObject>() {
            @Override
            public void accept(JsonObject jsonObject) {
                listBuilder.add(new FieldInfoBuilder()
                    .setPath(jsonObject.getString("path"))
                    .setFields(jsonObject.getJsonArray("fields").getList())
                    .createSqlField());
            }
        });

        return listBuilder.build();
    }
}
