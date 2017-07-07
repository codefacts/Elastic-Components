package tracker.server.interceptors.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.StatusCodes;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import tracker.model.UserModel;
import tracker.server.interceptors.AuthInterceptor;
import tracker.server.request.handlers.RequestProcessingErrorHandler;
import tracker.server.ServerUtils;
import tracker.server.component.AuthTokenGenerator;
import tracker.server.ex.RequestException;

import java.util.Objects;
import java.util.Set;

/**
 * Created by sohan on 7/3/2017.
 */
final public class AuthInterceptorImpl implements AuthInterceptor {
    final AuthTokenGenerator authTokenGenerator;
    final RequestProcessingErrorHandler requestProcessingErrorHandler;
    final Set<String> excludeUris;

    public AuthInterceptorImpl(AuthTokenGenerator authTokenGenerator, RequestProcessingErrorHandler requestProcessingErrorHandler, Set<String> excludeUris) {
        this.excludeUris = excludeUris;
        Objects.requireNonNull(authTokenGenerator);
        Objects.requireNonNull(requestProcessingErrorHandler);
        this.authTokenGenerator = authTokenGenerator;
        this.requestProcessingErrorHandler = requestProcessingErrorHandler;
    }

    @Override
    public void handle(RoutingContext ctx) {

        System.out.println("Auth Interceptor");

        if (isInExcludeUris(ctx.request().uri())) {

            ctx.put(ServerUtils.CURRENT_USER, new JsonObject(ImmutableMap.of(
                UserModel.userId, ServerUtils.ANONYMOUS
            )));

            ctx.next();
            
            return;
        }

        authTokenGenerator.getData(
            parseToken(ctx.request().getHeader(HttpHeaderNames.AUTHORIZATION))
        ).then(jsonObject -> {

            ctx.put(ServerUtils.CURRENT_USER, jsonObject);

            ctx.next();

        }).err(throwable -> requestProcessingErrorHandler.handleError(throwable, ctx));
    }

    private boolean isInExcludeUris(String uri) {
        return excludeUris.stream().anyMatch(uri::endsWith);
    }

    private String parseToken(String header) {
        if (header == null) {
            throw new RequestException(HttpResponseStatus.UNAUTHORIZED.code(), StatusCodes.authorizationError, "Unauthorized access, Please login to authenticate");
        }
        return header.substring(ServerUtils.BEARER.length());
    }
}
