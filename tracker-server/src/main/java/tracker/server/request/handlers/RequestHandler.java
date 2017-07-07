package tracker.server.request.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import tracker.server.request.handlers.impl.DelegatingRequestHandlerImpl;

/**
 * Created by sohan on 7/1/2017.
 */
public interface RequestHandler extends Handler<RoutingContext> {
    @Override
    void handle(RoutingContext ctx);

    static RequestHandler create(RequestHandler requestHandler, RequestProcessingErrorHandler requestProcessingErrorHandler) {
        return new DelegatingRequestHandlerImpl(requestHandler, requestProcessingErrorHandler);
    }
}
