package tracker.server.request.handlers.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.model.response.ErrorModel;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tracker.StatusCodes;
import tracker.server.component.ex.AuthTokenGeneratorException;
import tracker.server.ex.RequestException;
import tracker.server.request.handlers.RequestProcessingErrorHandler;

/**
 * Created by sohan on 7/3/2017.
 */
final public class RequestProcessingErrorHandlerImpl implements RequestProcessingErrorHandler {
    private static final Logger log = LogManager.getLogger(RequestProcessingErrorHandlerImpl.class);

    @Override
    public void handleError(Throwable ex, RoutingContext ctx) {

        ex.printStackTrace();
        log.error("Error Processing Request", ex);

        if (ex instanceof RequestException) {

            RequestException requestException = (RequestException) ex;

            ctx.response().setStatusCode(requestException.getHttpStatusCode());
            ctx.response().end(requestException.toJsonResponse().encode());
            return;

        } else if (ex instanceof AuthTokenGeneratorException) {

            ctx.response().setStatusCode(HttpResponseStatus.UNAUTHORIZED.code());
            ctx.response().end(new JsonObject(
                ImmutableMap.of(
                    ErrorModel.statusCode, StatusCodes.authorizationError,
                    ErrorModel.message, "Auth Token is not valid"
                )
            ).encode());
            return;
        }

        ctx.response().setStatusCode(HttpResponseStatus.INTERNAL_SERVER_ERROR.code());
        ctx.response().end(jsonResponse(ex.getMessage()));
    }

    private String jsonResponse(String message) {

        return new JsonObject(ImmutableMap.of(
            ErrorModel.statusCode, StatusCodes.badRequestError,
            ErrorModel.message, message != null ? message : "Unexpected error found when processing request"
        )).toString();
    }
}
