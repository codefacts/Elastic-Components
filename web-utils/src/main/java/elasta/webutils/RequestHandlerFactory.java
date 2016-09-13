package elasta.webutils;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

import java.util.function.BiFunction;

/**
 * Created by Jango on 9/12/2016.
 */
@FunctionalInterface
public interface RequestHandlerFactory extends BiFunction<String, HttpMethod, Handler<RoutingContext>> {
}
