package tracker.server.request.handlers.impl;

import elasta.composer.MessageBus;
import io.vertx.ext.web.RoutingContext;
import tracker.server.ServerUtils;
import tracker.server.generators.request.MessageBodyGenerator;
import tracker.server.request.handlers.DispatchingRequestHandler;
import tracker.server.generators.response.HttpResponseGenerator;
import tracker.server.request.handlers.JoDispatchingRequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;

/**
 * Created by sohan on 7/3/2017.
 */
final public class JoDispatchingRequestHandlerImpl implements JoDispatchingRequestHandler {
    final DispatchingRequestHandler dispatchingRequestHandler;

    public JoDispatchingRequestHandlerImpl(HttpResponseGenerator httpResponseGenerator, RequestProcessingErrorHandler requestProcessingErrorHandler, MessageBus messageBus, String messageAddress) {
        dispatchingRequestHandler = new DispatchingRequestHandlerImpl(
            RoutingContext::getBodyAsJson,
            httpResponseGenerator,
            requestProcessingErrorHandler,
            messageBus,
            messageAddress,
            ServerUtils.APPLICATION_JSON
        );
    }

    @Override
    public void handle(RoutingContext ctx) {

        dispatchingRequestHandler.handle(ctx);

    }
}
