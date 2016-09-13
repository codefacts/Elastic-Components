package elasta.webutils;

import com.google.common.collect.ImmutableList;
import elasta.vertxutils.VertxUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by Jango on 9/12/2016.
 */
public class RouteGenerator {

    public static final String HANDLER_FACTORY_MODULE_NAME = "handlerFactoryModuleName";
    public static final String BODY_HANDLER_FACTORY_MODULE_NAME = "bodyHandlerFactoryModuleName";

    public static RequestHandlerFactory defaultHandlerFactory(VertxUtils vertxUtils, WebUtils webUtils) {
        return (address, httpMethod) ->
            ctx -> {

                JsonObject jsonReq = new JsonObject()
                    .put(ReqCnst.PARAMS, webUtils.toJson(ctx.request().params()))
                    .put(ReqCnst.HEADERS, webUtils.toJson(ctx.request().headers()));

                if (ctx.getBody() != null && ctx.getBody().length() > 0) {
                    jsonReq.put(ReqCnst.BODY, ctx.getBodyAsJson());
                }

                vertxUtils.sendAndReceiveJsonObject(address, jsonReq)
                    .then(val -> ctx.response().end(val.encode()))
                ;
            };
    }

    private final BiFunction<String, HttpMethod, Handler<RoutingContext>> bodyHandlerFactory;
    private final BiFunction<String, HttpMethod, Handler<RoutingContext>> handlerFactory;
    private final EventNameGenerator eventNameGenerator;

    public RouteGenerator(BiFunction<String, HttpMethod, Handler<RoutingContext>> bodyHandlerFactory,
                          BiFunction<String, HttpMethod, Handler<RoutingContext>> handlerFactory, EventNameGenerator eventNameGenerator) {
        this.bodyHandlerFactory = bodyHandlerFactory;
        this.handlerFactory = handlerFactory;
        this.eventNameGenerator = eventNameGenerator;
    }

    public List<RouteSpec> makeRoutes(String uriPath, String resourceName) {
        ImmutableList.Builder<RouteSpec> builder = ImmutableList.builder();

        final String resourcePath = uriPath + "/" + resourceName;
        final String pathId = uriPath + "/" + resourceName + "/:id";

        builder.add(new RouteSpec(resourcePath, HttpMethod.GET, handlerFactory.apply(eventNameGenerator.eventName(EventHandlers.FIND_ALL, resourceName), HttpMethod.GET)));

        builder.add(new RouteSpec(pathId, HttpMethod.GET, handlerFactory.apply(eventNameGenerator.eventName(EventHandlers.FIND, resourceName), HttpMethod.GET)));

        builder.add(new RouteSpec(resourcePath, HttpMethod.POST, bodyHandlerFactory.apply(eventNameGenerator.eventName(EventHandlers.CREATE, resourceName), HttpMethod.POST)));
        builder.add(new RouteSpec(resourcePath, HttpMethod.POST, handlerFactory.apply(eventNameGenerator.eventName(EventHandlers.CREATE, resourceName), HttpMethod.POST)));

        builder.add(new RouteSpec(pathId, HttpMethod.PUT, bodyHandlerFactory.apply(eventNameGenerator.eventName(EventHandlers.UPDATE_ALL_PROPERTIES, resourceName), HttpMethod.PUT)));
        builder.add(new RouteSpec(pathId, HttpMethod.PUT, handlerFactory.apply(eventNameGenerator.eventName(EventHandlers.UPDATE_ALL_PROPERTIES, resourceName), HttpMethod.PUT)));

        builder.add(new RouteSpec(pathId, HttpMethod.PATCH, bodyHandlerFactory.apply(eventNameGenerator.eventName(EventHandlers.UPDATE_SOME_PROPERTIES, resourceName), HttpMethod.PATCH)));
        builder.add(new RouteSpec(pathId, HttpMethod.PATCH, handlerFactory.apply(eventNameGenerator.eventName(EventHandlers.UPDATE_SOME_PROPERTIES, resourceName), HttpMethod.PATCH)));

        builder.add(new RouteSpec(pathId, HttpMethod.DELETE, bodyHandlerFactory.apply(eventNameGenerator.eventName(EventHandlers.DELETE, resourceName), HttpMethod.DELETE)));
        builder.add(new RouteSpec(pathId, HttpMethod.DELETE, handlerFactory.apply(eventNameGenerator.eventName(EventHandlers.DELETE, resourceName), HttpMethod.DELETE)));

        return builder.build();
    }

    public void registerRoutes(Router router, Collection<RouteSpec> routeSpecs) {
        for (RouteSpec routeSpec : routeSpecs) {
            router.route(routeSpec.getHttpMethod(), routeSpec.getUri()).handler(routeSpec.getHandler());
        }
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
