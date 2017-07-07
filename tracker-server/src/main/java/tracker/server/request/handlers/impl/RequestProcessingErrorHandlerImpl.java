package tracker.server.request.handlers.impl;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.ext.web.RoutingContext;
import tracker.server.request.handlers.RequestProcessingErrorHandler;
import tracker.server.ex.RequestException;

/**
 * Created by sohan on 7/3/2017.
 */
final public class RequestProcessingErrorHandlerImpl implements RequestProcessingErrorHandler {
    @Override
    public void handleError(Throwable ex, RoutingContext ctx) {

        ex.printStackTrace();

        if (ex instanceof RequestException) {

            RequestException requestException = (RequestException) ex;

            ctx.response().setStatusCode(requestException.getHttpStatusCode());
            ctx.response().end(requestException.toJsonResponse().encode());
            return;
        }

        ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        ctx.response().end(ex.getMessage());
    }
}
