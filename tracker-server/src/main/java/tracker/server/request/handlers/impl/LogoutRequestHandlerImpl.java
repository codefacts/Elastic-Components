package tracker.server.request.handlers.impl;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.ext.web.RoutingContext;
import tracker.server.component.AuthTokenGenerator;
import tracker.server.interceptors.impl.AuthInterceptorImpl;
import tracker.server.request.handlers.LogoutRequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;

import java.util.Objects;

/**
 * Created by sohan on 7/12/2017.
 */
final public class LogoutRequestHandlerImpl implements LogoutRequestHandler {
    final AuthTokenGenerator authTokenGenerator;
    final RequestProcessingErrorHandler requestProcessingErrorHandler;

    public LogoutRequestHandlerImpl(AuthTokenGenerator authTokenGenerator, RequestProcessingErrorHandler requestProcessingErrorHandler) {
        Objects.requireNonNull(authTokenGenerator);
        Objects.requireNonNull(requestProcessingErrorHandler);
        this.authTokenGenerator = authTokenGenerator;
        this.requestProcessingErrorHandler = requestProcessingErrorHandler;
    }

    @Override
    public void handle(RoutingContext ctx) {

        authTokenGenerator
            .invalidate(
                AuthInterceptorImpl.parseToken(
                    ctx.request().headers().get(HttpHeaderNames.AUTHORIZATION)
                )
            )
            .then(aVoid -> ctx.response().end())
            .err(throwable -> requestProcessingErrorHandler.handleError(throwable, ctx));
    }
}
