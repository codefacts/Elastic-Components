package tracker.server.request.handlers.impl;

import elasta.composer.MessageBus;
import io.vertx.ext.web.RoutingContext;
import tracker.server.ServerUtils;
import tracker.server.generators.request.MessageHeaderGenerator;
import tracker.server.request.handlers.DispatchingRequestHandler;
import tracker.server.request.handlers.JaDispatchingRequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;
import tracker.server.generators.response.HttpResponseGenerator;

/**
 * Created by sohan on 7/4/2017.
 */
final public class JaDispatchingRequestHandlerImpl implements JaDispatchingRequestHandler {
    final DispatchingRequestHandler dispatchingRequestHandler;

    public JaDispatchingRequestHandlerImpl(MessageHeaderGenerator messageHeaderGenerator, HttpResponseGenerator httpResponseGenerator, RequestProcessingErrorHandler requestProcessingErrorHandler, MessageBus messageBus, String messageAddress) {
        dispatchingRequestHandler = new DispatchingRequestHandlerImpl(
            RoutingContext::getBodyAsJsonArray,
            messageHeaderGenerator,
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
