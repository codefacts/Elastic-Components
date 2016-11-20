package elasta.composer.module_exporter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.Resource;
import elasta.composer.ResourceBuilder;
import elasta.composer.EventToFlowDispatcher;
import elasta.composer.pipeline.transformation.JoJoTransformation;
import elasta.composer.pipeline.transformation.impl.json.object.TrimStringsTransformation;
import elasta.composer.pipeline.transformation.impl.JoJoTransformationImpl;
import elasta.composer.pipeline.transformation.impl.json.object.RemoveNullsTransformation;
import elasta.core.eventbus.SimpleEventBus;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.module.ModuleSystem;
import elasta.webutils.app.RequestCnsts;
import io.vertx.core.json.JsonObject;

import java.util.Map;

import static elasta.composer.ComposerUtils.emptyJsonObject;
import static elasta.core.flow.Flow.next;
import static elasta.core.flow.Flow.on;
import static elasta.core.flow.Flow.triggerNext;

/**
 * Created by Jango on 11/13/2016.
 */
public class ComposerExporterImpl implements ComposerExporter {

    @Override
    public void exportTo(ModuleSystem moduleSystem) {

        moduleSystem.export(EventToFlowDispatcher.class, module -> module.export(
            (jsonObject, event, params) -> {

                JsonObject meta = jsonObject.getJsonObject(RequestCnsts.META);

                meta.put(
                    EventToFlowDispatcher.EVENT,
                    event
                );

                params.getMap().forEach(meta::put);

                return module.require(Flow.class, Flows.EVENT_HANDLER_FLOW)
                    .start(jsonObject);
            }
        ));

        moduleSystem.export(Flow.class, Flows.EVENT_HANDLER_FLOW, module -> module.export(
            Flow.builder()
                .when(States.START, next(States.INITIAL_TRANSFORMATION))
                .when(States.INITIAL_TRANSFORMATION, next(States.CONVERTION))
                .when(States.CONVERTION,
                    on(Events.CONVERTION_ERROR, States.CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION),
                    next(States.VALIDATION)
                )
                .when(States.CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION, next(States.VALIDATION_RESULT_PREPERATION))
                .when(States.VALIDATION,
                    on(Events.VALIDATION_ERROR, States.VALIDATION_RESULT_PREPERATION),
                    next(States.TRANSFORMATION)
                )
                .when(States.VALIDATION_RESULT_PREPERATION, next(States.END))
                .when(States.TRANSFORMATION, next(States.ACTION))
                .when(States.ACTION, next(States.RESULT_PREPERATION))
                .when(States.RESULT_PREPERATION, next(States.END))
                .when(States.END, Flow.end())
                .initialState(States.START)

                .handlersP(States.START, module.require(JsonEnterHanler.class, FLowEnterHandlers.START))
                .handlersP(States.INITIAL_TRANSFORMATION, module.require(JsonEnterHanler.class, FLowEnterHandlers.INITIAL_TRANSFORMATION))
                .handlersP(States.CONVERTION, module.require(JsonEnterHanler.class, FLowEnterHandlers.CONVERTION))
                .handlersP(States.CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION, module.require(JsonEnterHanler.class, FLowEnterHandlers.CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION))
                .handlersP(States.VALIDATION, module.require(JsonEnterHanler.class, FLowEnterHandlers.VALIDATION))
                .handlersP(States.VALIDATION_RESULT_PREPERATION, module.require(JsonEnterHanler.class, FLowEnterHandlers.VALIDATION_ERROR))
                .handlersP(States.TRANSFORMATION, module.require(JsonEnterHanler.class, FLowEnterHandlers.TRANSFORMATION))
                .handlersP(States.ACTION, module.require(JsonEnterHanler.class, FLowEnterHandlers.ACTION))
                .handlersP(States.RESULT_PREPERATION, module.require(JsonEnterHanler.class, FLowEnterHandlers.RESULT_PREPERATION))
                .handlersP(States.END, module.require(JsonEnterHanler.class, FLowEnterHandlers.END))

                .build()
        ));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.START, module -> module.export(jsonObject -> Promises.just(triggerNext(jsonObject))));
        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.INITIAL_TRANSFORMATION, module -> module.export(
            jsonObject -> Promises.just(
                Flow.triggerNext(module.require(JoJoTransformation.class, JoJoTransformations.INITIAL_TRANSFORMATION).transform(jsonObject))
            )
        ));

        moduleSystem.export(JoJoTransformation.class, JoJoTransformations.INITIAL_TRANSFORMATION, module -> module
            .export(new JoJoTransformationImpl(ImmutableList.of(
                new TrimStringsTransformation(),
                new RemoveNullsTransformation(),
                val -> {

                    JsonObject params = val.getJsonObject(RequestCnsts.META).getJsonObject(RequestCnsts.PARAMS, emptyJsonObject());

                    JsonObject projection = params
                        .getJsonObject(DbReqParams.PROJECTION, emptyJsonObject());

                    ImmutableList.Builder<JsonObject> projectionListBuilder = ImmutableList.builder();
                    projection.fieldNames().forEach(name -> {
                        projectionListBuilder.add(
                            new JsonObject(
                                ImmutableMap.of(
                                    Criterias.PATH, name.equalsIgnoreCase(Criterias.ROOT) ? "" : name,
                                    Criterias.FIELDS, projection.getJsonArray(name)
                                )
                            )
                        );
                    });

                    val.put(DbReqParams.CRITERIA,
                        params
                            .getJsonObject(DbReqParams.CRITERIA, emptyJsonObject())

                    ).put(DbReqParams.PROJECTION,
                        projectionListBuilder.build()
                    );

                    params.remove(DbReqParams.CRITERIA);
                    params.remove(DbReqParams.PROJECTION);

                    return val;
                }
            ))));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.CONVERTION, module -> module.export(jsonObject -> {
            if (jsonObject.containsKey("id")) {
                jsonObject.put("id", Long.parseLong(jsonObject.getString("id")));
            }

            if (jsonObject.getJsonObject(DbReqParams.CRITERIA, emptyJsonObject()).containsKey("id")) {
                jsonObject.getJsonObject(DbReqParams.CRITERIA).put("id", Integer.parseInt(jsonObject.getJsonObject(DbReqParams.CRITERIA).getString("id")));
            }

            return Promises.just(Flow.triggerNext(jsonObject));
        }));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.CONVERTION_ERROR_TO_VALIDATION_ERROR_TRANSLATION, module -> module.export(
            jsonObject -> Promises.just(
                Flow.triggerNext(jsonObject)
            )
        ));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.VALIDATION, module -> module.export(
            jsonObject -> Promises.just(Flow.triggerNext(jsonObject))
        ));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.VALIDATION_ERROR, module -> module.export(
            jsonObject -> Promises.just(
                Flow.triggerNext(jsonObject)
            )
        ));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.TRANSFORMATION, module -> module.export(jsonObject -> Promises.just(Flow.triggerNext(jsonObject))));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.ACTION, module -> module.export(
            jsonObject -> {

                JsonObject meta = jsonObject.getJsonObject(RequestCnsts.META);

                Resource resource = (Resource) module.require(Map.class, Configs.RESOURCE_BY_EVENT_PATH_MAP)
                    .get(
                        meta.getString(EventToFlowDispatcher.RESOURCE)
                    );

                String action = meta.getString(EventToFlowDispatcher.ACTION);

                final String event = DbEvents.DB + "." + action;

                JsonObject dbRequest = event.startsWith(DbEvents.DB_CREATE) || event.startsWith(DbEvents.DB_UPDATE)
                    ? new JsonObject(
                    ImmutableMap.of(
                        DbReqParams.ENTITY, resource.getEntity(),
                        DbReqParams.DATA, jsonObject
                    )
                ) : new JsonObject(
                    ImmutableMap.<String, Object>builder()
                        .put(DbReqParams.ENTITY, resource.getEntity())
                        .putAll(jsonObject)
                        .build()
                );

                return module.require(SimpleEventBus.class).<JsonObject>fire(event, dbRequest).map(entries -> {
                    if (action.startsWith("find")) {
                        return Flow.triggerNext(entries);
                    }
                    return Flow.triggerNext(jsonObject);
                });
            }
        ));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.RESULT_PREPERATION, module -> module.export(
            jsonObject -> Promises.just(Flow.triggerNext(jsonObject))
        ));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.END, module -> module.export(
            jsonObject -> Promises.just(Flow.triggerNext(jsonObject))
        ));

        moduleSystem.export(Map.class, Configs.RESOURCE_BY_EVENT_PATH_MAP, module -> module.export(ImmutableMap.of(
            "tablet", new ResourceBuilder()
                .setName("tablet")
                .setDisplayName("Tablet")
                .setEntity("Tablet")
                .build(),
            "br", new ResourceBuilder()
                .setName("br")
                .setDisplayName("Business Representative")
                .build()
        )));
    }
}
