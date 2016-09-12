package elasta.webutils;

import elasta.module.ModuleSystem;
import elasta.vertxutils.VertxUtils;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * Created by Jango on 9/12/2016.
 */
public interface WebUtilsExporter {

    void export(ModuleSystem moduleSystem);

    static WebUtilsExporter get() {
        return moduleSystem -> {

            moduleSystem.export(WebUtils.class, module -> {
                module.export(new WebUtilsImpl());
            });

            moduleSystem.export(EventNameGenerator.class, module -> {
                module.export((event, resourceName) -> event + "/" + resourceName);
            });

            moduleSystem.export(RoutingContextHandlerFactory.class, RouteGenerators.BODY_HANDLER_FACTORY_MODULE_NAME, module -> {
                module.export((s, httpMethod) -> BodyHandler.create());
            });

            moduleSystem.export(RoutingContextHandlerFactory.class, RouteGenerators.HANDLER_FACTORY_MODULE_NAME, module -> {
                module.export(RouteGenerators.defaultHandlerFactory(module.require(VertxUtils.class), module.require(WebUtils.class)));
            });

            moduleSystem.export(RouteGenerators.class,
                module ->
                    module.export(
                        new RouteGenerators(
                            module.require(RoutingContextHandlerFactory.class, RouteGenerators.BODY_HANDLER_FACTORY_MODULE_NAME),
                            module.require(RoutingContextHandlerFactory.class, RouteGenerators.HANDLER_FACTORY_MODULE_NAME),
                            module.require(EventNameGenerator.class))
                    ));

            moduleSystem.export(EventHandlerGenerator.class,
                module ->
                    module.export(
                        new EventHandlerGenerator(
                            module.require(EventBus.class),
                            module.moduleSystem(),
                            module.require(EventNameGenerator.class)
                        )
                    ));
        };
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
