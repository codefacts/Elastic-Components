package tracker.server.impl;

import io.vertx.ext.web.RoutingContext;
import tracker.server.RequestHandler;

import java.util.Objects;

/**
 * Created by sohan on 7/1/2017.
 */
final public class RequestHandlerImpl implements RequestHandler {
    final RequestHandler requestHandler;

    public RequestHandlerImpl(RequestHandler requestHandler) {
        Objects.requireNonNull(requestHandler);
        this.requestHandler = requestHandler;
    }

    @Override
    public void handle(RoutingContext ctx) {
        try {


        } catch (Exception ex) {
            ctx.fail(ex);
        }
    }
}
