package tracker.server.request.handlers.impl;

import elasta.commons.Utils;
import elasta.composer.MessageBus;
import elasta.composer.model.response.ErrorModel;
import elasta.core.promise.impl.Promises;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.MultiMap;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import tracker.model.UserModel;
import tracker.server.generators.request.MessageHeaderGenerator;
import tracker.server.ServerUtils;
import tracker.server.StatusCodes;
import tracker.server.ex.RequestException;
import tracker.server.request.handlers.DispatchingRequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;
import tracker.server.generators.response.HttpResponseGenerator;
import tracker.server.generators.request.MessageBodyGenerator;

import java.util.Objects;

/**
 * Created by sohan on 7/4/2017.
 */
final public class DispatchingRequestHandlerImpl implements DispatchingRequestHandler {
    final MessageBodyGenerator messageBodyGenerator;
    final MessageHeaderGenerator messageHeaderGenerator;
    final HttpResponseGenerator httpResponseGenerator;
    final RequestProcessingErrorHandler requestProcessingErrorHandler;
    final MessageBus messageBus;
    final String messageAddress;
    final String expectedContentTypeStartsWith;

    public DispatchingRequestHandlerImpl(MessageBodyGenerator messageBodyGenerator, MessageHeaderGenerator messageHeaderGenerator, HttpResponseGenerator httpResponseGenerator, RequestProcessingErrorHandler requestProcessingErrorHandler, MessageBus messageBus, String messageAddress) {
        this(messageBodyGenerator, messageHeaderGenerator, httpResponseGenerator, requestProcessingErrorHandler, messageBus, messageAddress, "");
    }

    public DispatchingRequestHandlerImpl(MessageBodyGenerator messageBodyGenerator, MessageHeaderGenerator messageHeaderGenerator, HttpResponseGenerator httpResponseGenerator, RequestProcessingErrorHandler requestProcessingErrorHandler, MessageBus messageBus, String messageAddress, String expectedContentTypeStartsWith) {
        Objects.requireNonNull(messageBodyGenerator);
        Objects.requireNonNull(messageHeaderGenerator);
        Objects.requireNonNull(httpResponseGenerator);
        Objects.requireNonNull(requestProcessingErrorHandler);
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(messageAddress);
        Objects.requireNonNull(expectedContentTypeStartsWith);
        this.messageBodyGenerator = messageBodyGenerator;
        this.messageHeaderGenerator = messageHeaderGenerator;
        this.httpResponseGenerator = httpResponseGenerator;
        this.requestProcessingErrorHandler = requestProcessingErrorHandler;
        this.messageBus = messageBus;
        this.messageAddress = messageAddress;
        this.expectedContentTypeStartsWith = expectedContentTypeStartsWith;
    }

    @Override
    public void handle(RoutingContext ctx) {

        String contentTypeHeader = ctx.request().getHeader(HttpHeaderNames.CONTENT_TYPE);

        if (ctx.request().method() == HttpMethod.GET) {
            processRequest(ctx);
            return;
        }

        if (contentTypeHeader == null || contentTypeHeader.isEmpty()) {
            throw new RequestException(StatusCodes.contentTypeMissingError, "Content Type is missing");
        }

        if (Utils.not(contentTypeHeader.startsWith(expectedContentTypeStartsWith))) {
            throw new RequestException(StatusCodes.contentTypeMissingError, "Invalid content type header '" + contentTypeHeader + "'");
        }

        processRequest(ctx);

    }

    private void processRequest(RoutingContext ctx) {

        final Object reqBody = messageBodyGenerator.generate(ctx);

        final MultiMap headers = messageHeaderGenerator.generate(ctx);

        Objects.requireNonNull(reqBody);
        Objects.requireNonNull(headers);

        messageBus.sendAndReceive(
            MessageBus.Params.builder()
                .address(messageAddress)
                .message(reqBody)
                .userId(ctx.<JsonObject>get(ServerUtils.CURRENT_USER).getString(UserModel.userId))
                .options(new DeliveryOptions().setHeaders(headers))
                .build()
        ).thenP(message -> {

            Object resBody = message.body();

            if (resBody instanceof JsonObject && ((JsonObject) resBody).getString(ErrorModel.statusCode) != null) {

                return Promises.error(new RequestException(((JsonObject) resBody)));
            }

            HttpResponseGenerator.HttpResponse httpResponse = httpResponseGenerator.generate(resBody);

            Objects.requireNonNull(httpResponse);

            ctx.response().setStatusCode(httpResponse.getStatusCode());
            httpResponse.getHeaders().forEach((header, value) -> ctx.response().putHeader(header, value));
            ctx.response().end(httpResponse.getBody());

            return Promises.empty();

        }).err(throwable -> requestProcessingErrorHandler.handleError(throwable, ctx));
    }
}
