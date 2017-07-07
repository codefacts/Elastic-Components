package tracker.server.request.handlers;

import io.vertx.ext.web.RoutingContext;

/**
 * Created by sohan on 7/3/2017.
 */
@FunctionalInterface
public interface RequestProcessingErrorHandler {
    void handleError(Throwable ex, RoutingContext ctx);
}
