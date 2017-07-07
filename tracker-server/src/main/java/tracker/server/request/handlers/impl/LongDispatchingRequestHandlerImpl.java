package tracker.server.request.handlers.impl;

import elasta.composer.MessageBus;
import io.vertx.ext.web.RoutingContext;
import tracker.server.ServerUtils;
import tracker.server.request.handlers.DispatchingRequestHandler;
import tracker.server.request.handlers.ObjectDispatchingRequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;
import tracker.server.generators.response.HttpResponseGenerator;
import tracker.server.generators.request.MessageBodyGenerator;

import java.util.Objects;

/**
 * Created by sohan on 7/4/2017.
 */
final public class LongDispatchingRequestHandlerImpl implements ObjectDispatchingRequestHandler {
    final DispatchingRequestHandler dispatchingRequestHandler;
    final String pathParameterName;

    public LongDispatchingRequestHandlerImpl(HttpResponseGenerator httpResponseGenerator, RequestProcessingErrorHandler requestProcessingErrorHandler, MessageBus messageBus, String messageAddress, String pathParameterName) {
        this.dispatchingRequestHandler = new DispatchingRequestHandlerImpl(
            parseLong(),
            httpResponseGenerator,
            requestProcessingErrorHandler,
            messageBus,
            messageAddress,
            ServerUtils.TEXT_PLAIN
        );

        Objects.requireNonNull(pathParameterName);
        this.pathParameterName = pathParameterName;
    }

    private MessageBodyGenerator<Long> parseLong() {
        return ctx -> Long.parseLong(ctx.pathParam(pathParameterName));
    }

    @Override
    public void handle(RoutingContext ctx) {

    }
}
