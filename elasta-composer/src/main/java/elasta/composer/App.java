package elasta.composer;

import com.google.common.collect.ImmutableMap;
import elasta.core.eventbus.SimpleEventBus;
import elasta.core.eventbus.impl.SimpleEventBusImpl;
import elasta.core.promise.impl.Promises;
import elasta.module.ModuleSystem;
import elasta.webutils.app.DefaultValues;
import elasta.webutils.app.RequestHandler;
import elasta.webutils.app.UriToEventTranslator;
import elasta.webutils.app.module.exporter.ModuleExporterWebUtils;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

/**
 * Created by Jango on 11/9/2016.
 */
public class App {

    public static void main(String[] args) {

        final ModuleSystem moduleSystem = ModuleSystem.create();

        ModuleExporterWebUtils.get().exportModule(moduleSystem);

        moduleSystem.export(SimpleEventBus.class, module -> module.export(new SimpleEventBusImpl()));

        moduleSystem.export(UriToEventTranslator.class, module -> module.export(
            (UriToEventTranslator<JsonObject>) requestInfo -> (requestInfo.getUri().substring(1).replace('/', '.') + "." + module.require(DefaultValues.class)
                .httpMethodToActionMap().get(requestInfo.getHttpMethod())).toLowerCase()
        ));

        final Vertx vertx = Vertx.vertx();

        Router router = Router.router(vertx);

//        router.get().handler(event -> event.response().end("ok"));

        router.get("/students").handler(moduleSystem.require(RequestHandler.class));
        router.post("/students").handler(moduleSystem.require(RequestHandler.class));
        router.put("/students").handler(moduleSystem.require(RequestHandler.class));
        router.delete("/students").handler(moduleSystem.require(RequestHandler.class));

        registerEventHandlers(moduleSystem.require(SimpleEventBus.class));


        System.out.println("Server Started.");
    }

    private static void registerEventHandlers(SimpleEventBus eventBus) {

        eventBus.addListener("{}.*", (o, context) -> Promises.just(new JsonObject(
            ImmutableMap.of("ok", "1", "msg", "success")
        )));
    }
}
