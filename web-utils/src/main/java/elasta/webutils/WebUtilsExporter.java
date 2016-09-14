package elasta.webutils;

import elasta.module.ModuleSystem;
import elasta.vertxutils.SenderUtils;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;
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
                module.export(RouteGenerator.defaultHandlerFactory(module.require(SenderUtils.class), module.require(WebUtils.class)));
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
                            module.require(EventNameGenerator.class),
                            module.require(EventHandlerBuilder.class)
                        )
                    ));

            moduleSystem.export(CrudBuilder.class,
                module ->
                    module.export((router, prefixUri, resourceName, machineMap) -> {
                        RouteGenerator routeGenerator = module.require(RouteGenerator.class);
                        EventHandlerGenerator handlerGenerator = module.require(EventHandlerGenerator.class);

                        List<RouteSpec> routeSpecs = routeGenerator.makeRoutes(prefixUri, resourceName);
                        List<EventSpec> eventSpecs = handlerGenerator.makeHandlers(resourceName, machineMap);

                        routeGenerator.registerRoutes(router, routeSpecs);
                        handlerGenerator.registerHandlers(eventSpecs);
                    }));

            moduleSystem.export(RegisterFilters.class, module -> {
                module.export(router -> {

                });
            });

            moduleSystem.export(StateMachineStarter.class, module -> {
                module.export(
                    (machine, body, headers, address, replyAddress) ->
                        machine.start(
                            body
                                .put(MsgCnst.$HEADERS, module.require(WebUtils.class).toJson(headers))
                                .put(MsgCnst.$ADDRESS, address)
                                .put(MsgCnst.$REPLY_ADDRESS, replyAddress)
                        )
                );
            });

            moduleSystem.export(EventHandlerBuilder.class, module -> {
                module.export(
                    machine ->
                        message ->
                            module.require(SenderUtils.class)
                                .handleMessage(message,
                                    (body, headers, address, replyAddress) ->
                                        module.require(StateMachineStarter.class)
                                            .startMachine(machine, (JsonObject) body, headers, address, replyAddress)
                                )
                );
            });
        };
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
