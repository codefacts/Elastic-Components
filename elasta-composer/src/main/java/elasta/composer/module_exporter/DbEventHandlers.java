package elasta.composer.module_exporter;

import com.google.common.collect.ImmutableMap;
import elasta.composer.Utils;
import elasta.core.eventbus.SimpleEventBus;
import elasta.orm.Db;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 11/13/2016.
 */
public interface DbEventHandlers {
    static void registerHandlers(SimpleEventBus eventBus, Db db) {

        eventBus.<JsonObject, JsonObject>addInterceptorP(DbEvents.DB_CREATE,
            (jsonObject, event, params) ->
                db.insertOrUpdate(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.DATA)
                )
        );

        eventBus.<JsonObject, JsonObject>addInterceptorP(DbEvents.DB_UPDATE,
            (jsonObject, event, params) ->
                db.insertOrUpdate(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.DATA)
                )
        );

        eventBus.<JsonObject, JsonObject>addInterceptorP(DbEvents.DB_DELETE,
            (jsonObject, event, params) ->
                db.delete(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getValue(DbReqParams.ID))
                    .map(id -> jsonObject)
        );

        eventBus.<JsonObject, JsonObject>addInterceptorP(DbEvents.DB_FIND,
            (jsonObject, event, params) ->
                db.findOne(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getValue(DbReqParams.ID),
                    jsonObject
                        .getJsonArray(DbReqParams.FIELDS, Utils.emptyJsonArray()).getList())
        );

        eventBus.<JsonObject, JsonObject>addInterceptorP(DbEvents.DB_FIND_BY_PARAMS,
            (jsonObject, event, params) ->
                db.findOne(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.PARAMS),
                    jsonObject
                        .getJsonArray(DbReqParams.FIELDS, Utils.emptyJsonArray()).getList())
        );

        eventBus.<JsonObject, JsonObject>addInterceptorP(DbEvents.DB_FIND_ALL,
            (jsonObject, event, params) ->
                db.findAll(jsonObject.getString(DbReqParams.ENTITY),
                    jsonObject
                        .getJsonObject(DbReqParams.PARAMS),
                    jsonObject
                        .getJsonArray(DbReqParams.FIELDS, Utils.emptyJsonArray()).getList())
                    .map(jsonObjects -> new JsonObject(ImmutableMap.of(DbReqParams.DATA, jsonObjects)))
        );
    }
}
