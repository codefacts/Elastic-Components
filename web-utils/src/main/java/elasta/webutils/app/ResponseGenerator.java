package elasta.webutils.app;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/6/2016.
 */
public interface ResponseGenerator<T> {
    void reply(T t, RoutingContext context);
}
