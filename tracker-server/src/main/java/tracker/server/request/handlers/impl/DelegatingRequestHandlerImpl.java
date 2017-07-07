package tracker.server.request.handlers.impl;

import io.vertx.ext.web.RoutingContext;
import tracker.server.request.handlers.DelegatingRequestHandler;
import tracker.server.request.handlers.RequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;

import java.util.Objects;

/**
 * Created by sohan on 7/1/2017.
 */
final public class DelegatingRequestHandlerImpl implements DelegatingRequestHandler {
    final RequestHandler requestHandler;
    final RequestProcessingErrorHandler requestProcessingErrorHandler;

    public DelegatingRequestHandlerImpl(RequestHandler requestHandler, RequestProcessingErrorHandler requestProcessingErrorHandler) {
        Objects.requireNonNull(requestHandler);
        Objects.requireNonNull(requestProcessingErrorHandler);
        this.requestHandler = requestHandler;
        this.requestProcessingErrorHandler = requestProcessingErrorHandler;
    }

    @Override
    public void handle(RoutingContext ctx) {
        try {

            requestHandler.handle(ctx);

        } catch (Exception ex) {
            requestProcessingErrorHandler.handleError(ex, ctx);
        }
    }
}
