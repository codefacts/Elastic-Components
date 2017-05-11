package elasta.webutils;

import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/6/2016.
 */
public interface ResponseGenerator<T> {
    void reply(Message<T> message, RoutingContext context);
}
