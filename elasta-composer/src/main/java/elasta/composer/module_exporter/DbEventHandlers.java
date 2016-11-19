package elasta.composer.module_exporter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.ComposerUtils;
import elasta.core.eventbus.SimpleEventBus;
import elasta.orm.Db;
import elasta.orm.json.core.FieldInfo;
import elasta.orm.json.core.FieldInfoBuilder;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 11/13/2016.
 */
public interface DbEventHandlers {
    static void registerHandlers(SimpleEventBus eventBus, Db db) {

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_CREATE,
            (jsonObject, event, params) ->
                db.insertOrUpdate(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.DATA)
                )
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_UPDATE,
            (jsonObject, event, params) ->
                db.insertOrUpdate(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.DATA)
                )
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_DELETE,
            (jsonObject, event, params) ->
                db.delete(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getValue(DbReqParams.ID))
                    .map(id -> jsonObject)
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_FIND,
            (jsonObject, event, params) ->
                db.findOne(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getValue(DbReqParams.ID), convertToFields(
                        jsonObject
                            .getJsonArray(DbReqParams.PROJECTION, ComposerUtils.emptyJsonArray()).getList()
                    ))
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_FIND_BY_PARAMS,
            (jsonObject, event, params) ->
                db.findOne(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.CRITERIA),
                    convertToFields(
                        jsonObject
                            .getJsonArray(DbReqParams.PROJECTION, ComposerUtils.emptyJsonArray()).getList()
                    ))
        );

        eventBus.<JsonObject, JsonObject>addProcessorP(DbEvents.DB_FIND_ALL,
            (jsonObject, event, params) ->
                db.findAll(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.CRITERIA),
                    convertToFields(
                        jsonObject
                            .getJsonArray(DbReqParams.PROJECTION, ComposerUtils.emptyJsonArray()).getList()
                    ))
                    .map(jsonObjects -> new JsonObject(ImmutableMap.of(DbReqParams.DATA, jsonObjects)))
        );
    }

    static List<FieldInfo> convertToFields(List<JsonObject> list) {
        ImmutableList.Builder<FieldInfo> listBuilder = ImmutableList.builder();

        list.forEach(jsonObject -> listBuilder.add(new FieldInfoBuilder()
            .setPath(jsonObject.getString("path"))
            .setFields(jsonObject.getJsonArray("fields").getList())
            .createSqlField()));

        return listBuilder.build();
    }
}
