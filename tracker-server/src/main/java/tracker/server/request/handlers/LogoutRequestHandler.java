package tracker.server.request.handlers;

import io.vertx.ext.web.RoutingContext;

/**
 * Created by sohan on 7/12/2017.
 */
public interface LogoutRequestHandler extends RequestHandler {
    @Override
    void handle(RoutingContext ctx);
}
