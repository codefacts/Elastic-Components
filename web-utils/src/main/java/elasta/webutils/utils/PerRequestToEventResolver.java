package elasta.webutils.utils;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/17/2016.
 */
final public class PerRequestToEventResolver implements Handler<RoutingContext> {
    private final String event;

    public PerRequestToEventResolver(String event) {
        this.event = event;
    }

    @Override
    public void handle(RoutingContext context) {
        context.put("event", event);
        context.next();
    }
}
