package elasta.composer.module_exporter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.Cnsts;
import elasta.composer.Resource;
import elasta.composer.EntityBuilder;
import elasta.composer.EventToFlowDispatcher;
import elasta.composer.transformation.JsonTransformationPipeline;
import elasta.composer.transformation.impl.json.object.JoJoTransform;
import elasta.composer.transformation.impl.json.object.RemoveNullsTransformation;
import elasta.composer.transformation.impl.json.object.TrimStringsTransformation;
import elasta.core.eventbus.SimpleEventBus;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.module.ModuleSystem;
import elasta.webutils.app.DefaultValues;
import elasta.webutils.app.UriToEventTranslator;
import io.vertx.core.json.JsonObject;

import java.util.Map;

import static elasta.core.flow.Flow.next;
import static elasta.core.flow.Flow.on;
import static elasta.core.flow.Flow.triggerNext;

/**
 * Created by Jango on 11/13/2016.
 */
public class ComposerExporterImpl implements ComposerExporter {

    @Override
    public void exportTo(ModuleSystem moduleSystem) {

        moduleSystem.export(UriToEventTranslator.class, module -> module.export(
            (UriToEventTranslator<JsonObject>) requestInfo -> (Cnsts.API + "." + requestInfo.getUri().substring(1).replace('/', '.') + "." + module.require(DefaultValues.class)
                .httpMethodToActionMap().get(requestInfo.getHttpMethod())).toLowerCase()
        ));

        moduleSystem.export(EventToFlowDispatcher.class, module -> module.export(
            (jsonObject, event, params) -> {
                params.getMap().forEach(jsonObject::put);
                return module.require(Flow.class, Flows.EVENT_HANDLER_FLOW)
                    .start(
                        jsonObject
                            .put(
                                EventToFlowDispatcher.EVENT,
                                event
                            )
                    );
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
                Flow.triggerNext(module.require(JoJoTransform.class, JoJoTransformations.INITIAL_TRANSFORMATION).transform(jsonObject))
            )
        ));

        moduleSystem.export(JoJoTransform.class, JoJoTransformations.INITIAL_TRANSFORMATION, module -> module
            .export(new JsonTransformationPipeline(ImmutableList.of(
                new TrimStringsTransformation(),
                new RemoveNullsTransformation()
            ))));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.CONVERTION, module -> module.export(jsonObject -> Promises.just(Flow.triggerNext(jsonObject))));

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
                Resource resource = (Resource) module.require(Map.class, Configs.RESOURCE_BY_EVENT_PATH_MAP)
                    .get(
                        jsonObject.getString(EventToFlowDispatcher.ENTITY)
                    );

                String action = jsonObject.getString(EventToFlowDispatcher.ACTION);

                final String event = DbEvents.DB + "." + action;

                JsonObject dbRequest = event.startsWith(DbEvents.DB_CREATE) || event.startsWith(DbEvents.DB_UPDATE) ? new JsonObject(
                    ImmutableMap.of(
                        DbReqParams.ENTITY, resource.getName(),
                        DbReqParams.DATA, jsonObject
                    )
                ) : jsonObject;

                return module.require(SimpleEventBus.class).<JsonObject>fire(event, dbRequest).map(o -> Flow.triggerNext(jsonObject));
            }
        ));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.RESULT_PREPERATION, module -> module.export(
            jsonObject -> Promises.just(Flow.triggerNext(jsonObject))
        ));

        moduleSystem.export(JsonEnterHanler.class, FLowEnterHandlers.END, module -> module.export(
            jsonObject -> Promises.just(Flow.triggerNext(jsonObject))
        ));

        moduleSystem.export(Map.class, Configs.RESOURCE_BY_EVENT_PATH_MAP, module -> module.export(ImmutableMap.of(
            "students", new EntityBuilder()
                .setName("student")
                .setDisplayName("Student")
                .createEntity(),
            "books", new EntityBuilder()
                .setName("book")
                .setDisplayName("Book")
                .createEntity()
        )));
    }
}
