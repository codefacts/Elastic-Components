package elasta.webutils;

import elasta.module.ModuleSystem;
import elasta.vertxutils.VertxUtils;
import io.vertx.core.eventbus.EventBus;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.List;

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

            moduleSystem.export(RequestHandlerFactory.class, RouteGenerator.BODY_HANDLER_FACTORY_MODULE_NAME, module -> {
                module.export((s, httpMethod) -> BodyHandler.create());
            });

            moduleSystem.export(RequestHandlerFactory.class, RouteGenerator.HANDLER_FACTORY_MODULE_NAME, module -> {
                module.export(RouteGenerator.defaultHandlerFactory(module.require(VertxUtils.class), module.require(WebUtils.class)));
            });

            moduleSystem.export(RouteGenerator.class,
                module ->
                    module.export(
                        new RouteGenerator(
                            module.require(RequestHandlerFactory.class, RouteGenerator.BODY_HANDLER_FACTORY_MODULE_NAME),
                            module.require(RequestHandlerFactory.class, RouteGenerator.HANDLER_FACTORY_MODULE_NAME),
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

            moduleSystem.export(CrudBuilder.class,
                module ->
                    module.export((router, prefixUri, resourceName) -> {
                        RouteGenerator routeGenerator = module.require(RouteGenerator.class);
                        EventHandlerGenerator handlerGenerator = module.require(EventHandlerGenerator.class);

                        List<RouteSpec> routeSpecs = routeGenerator.makeRoutes(prefixUri, resourceName);
                        List<EventSpec> eventSpecs = handlerGenerator.makeHandlers(resourceName);

                        routeGenerator.registerRoutes(router, routeSpecs);
                        handlerGenerator.registerHandlers(eventSpecs);
                    }));

            moduleSystem.export(RegisterFilters.class, module -> {
                module.export(router -> {

                });
            });
        };
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
