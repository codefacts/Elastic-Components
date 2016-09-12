package elasta.webutils;

import com.google.common.collect.ImmutableList;
import elasta.vertxutils.VertxUtils;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Created by Jango on 9/12/2016.
 */
public class RouteUtils {

    public static BiFunction<String, HttpMethod, Handler<RoutingContext>> defaultHandlerFactory(VertxUtils vertxUtils, WebUtils webUtils) {
        return (address, httpMethod) ->
            ctx -> {

                JsonObject jsonReq = new JsonObject()
                    .put(ReqCnst.PARAMS, webUtils.toJson(ctx.request().params()))
                    .put(ReqCnst.HEADERS, webUtils.toJson(ctx.request().headers()));

                if (ctx.getBody().length() > 0) {
                    jsonReq.put(ReqCnst.BODY, ctx.getBodyAsJson());
                }

                vertxUtils.sendAndReceiveJsonObject(address, jsonReq)
                    .then(val -> ctx.response().end(val.encode()))
                ;
            };
    }

    private final BiFunction<String, HttpMethod, Handler<RoutingContext>> bodyHandlerFactory;
    private final BiFunction<String, HttpMethod, Handler<RoutingContext>> handlerFactory;

    public RouteUtils(BiFunction<String, HttpMethod, Handler<RoutingContext>> bodyHandlerFactory,
                      BiFunction<String, HttpMethod, Handler<RoutingContext>> handlerFactory) {
        this.bodyHandlerFactory = bodyHandlerFactory;
        this.handlerFactory = handlerFactory;
    }

    public List<RouteSpec> createRoutes(String uriPath, String resourceName) {
        ImmutableList.Builder<RouteSpec> builder = ImmutableList.builder();

        String pathId = uriPath + "/" + resourceName + "/:id";

        builder.add(new RouteSpec(uriPath + "/" + resourceName, HttpMethod.GET, handlerFactory.apply(resourceName + "/" + EventHandlers.FIND_ALL, HttpMethod.GET)));

        builder.add(new RouteSpec(pathId, HttpMethod.GET, handlerFactory.apply(resourceName + "/" + EventHandlers.FIND, HttpMethod.GET)));

        builder.add(new RouteSpec(pathId, HttpMethod.POST, bodyHandlerFactory.apply(resourceName + "/" + EventHandlers.CREATE, HttpMethod.POST)));
        builder.add(new RouteSpec(pathId, HttpMethod.POST, handlerFactory.apply(resourceName + "/" + EventHandlers.CREATE, HttpMethod.POST)));

        builder.add(new RouteSpec(pathId, HttpMethod.PUT, bodyHandlerFactory.apply(resourceName + "/" + EventHandlers.UPDATE_ALL_PROPERTIES, HttpMethod.PUT)));
        builder.add(new RouteSpec(pathId, HttpMethod.PUT, handlerFactory.apply(resourceName + "/" + EventHandlers.UPDATE_ALL_PROPERTIES, HttpMethod.PUT)));

        builder.add(new RouteSpec(pathId, HttpMethod.PATCH, bodyHandlerFactory.apply(resourceName + "/" + EventHandlers.UPDATE_SOME_PROPERTIES, HttpMethod.PATCH)));
        builder.add(new RouteSpec(pathId, HttpMethod.PATCH, handlerFactory.apply(resourceName + "/" + EventHandlers.UPDATE_SOME_PROPERTIES, HttpMethod.PATCH)));

        builder.add(new RouteSpec(pathId, HttpMethod.DELETE, bodyHandlerFactory.apply(resourceName + "/" + EventHandlers.DELETE, HttpMethod.DELETE)));
        builder.add(new RouteSpec(pathId, HttpMethod.DELETE, handlerFactory.apply(resourceName + "/" + EventHandlers.DELETE, HttpMethod.DELETE)));

        return builder.build();
    }

    public void registerRoutesTo(Router router, Collection<RouteSpec> routeSpecs) {
        for (RouteSpec routeSpec : routeSpecs) {
            router.route(routeSpec.getHttpMethod(), routeSpec.getUri()).handler(routeSpec.getHandler());
        }
    }
}
