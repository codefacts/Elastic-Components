package tracker.server.request.handlers;

import io.vertx.ext.web.RoutingContext;

/**
 * Created by sohan on 7/4/2017.
 */
public interface DispatchingRequestHandler extends RequestHandler {
    @Override
    void handle(RoutingContext ctx);
}
