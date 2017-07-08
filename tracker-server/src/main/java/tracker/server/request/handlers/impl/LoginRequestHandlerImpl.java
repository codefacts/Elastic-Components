package tracker.server.request.handlers.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.MessageBus;
import elasta.composer.model.response.ErrorModel;
import elasta.core.promise.impl.Promises;
import elasta.core.touple.immutable.Tpls;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import tracker.Addresses;
import tracker.TrackerUtils;
import tracker.server.request.handlers.LoginRequestHandler;
import tracker.server.request.handlers.RequestProcessingErrorHandler;
import tracker.server.component.AuthTokenGenerator;
import tracker.server.ex.RequestException;
import tracker.server.model.AuthModel;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static tracker.server.ServerUtils.timeDiff;

/**
 * Created by sohan on 7/1/2017.
 */
final public class LoginRequestHandlerImpl implements LoginRequestHandler {
    final Vertx vertx;
    final MessageBus messageBus;
    final AuthTokenGenerator authTokenGenerator;
    final RequestProcessingErrorHandler requestProcessingErrorHandler;

    public LoginRequestHandlerImpl(Vertx vertx, MessageBus messageBus, AuthTokenGenerator authTokenGenerator, RequestProcessingErrorHandler requestProcessingErrorHandler) {
        Objects.requireNonNull(vertx);
        Objects.requireNonNull(messageBus);
        Objects.requireNonNull(authTokenGenerator);
        Objects.requireNonNull(requestProcessingErrorHandler);
        this.vertx = vertx;
        this.messageBus = messageBus;
        this.authTokenGenerator = authTokenGenerator;
        this.requestProcessingErrorHandler = requestProcessingErrorHandler;
    }

    @Override
    public void handle(RoutingContext ctx) {

        JsonObject req = ctx.getBodyAsJson();

        Objects.requireNonNull(req);

        messageBus
            .sendAndReceiveJsonObject(
                MessageBus.Params.builder()
                    .address(Addresses.authenticate)
                    .message(req)
                    .userId(TrackerUtils.anonymous)
                    .build()
            )
            .mapP(message -> {

                final JsonObject user = message.body();

                if (user.containsKey(ErrorModel.statusCode)) {
                    return Promises.error(new RequestException(user));
                }

                return authTokenGenerator.generate(user).map(authToken -> Tpls.of(authToken, user));

            })
            .then(tpl2 -> tpl2.accept((authToken, jsonObject) -> {
                ctx.response().end(
                    new JsonObject(
                        ImmutableMap.<String, Object>builder()
                            .putAll(jsonObject.getMap())
                            .put(AuthModel.authToken, authToken.getAuthToken())
                            .put(AuthModel.expireIn, timeDiff(authToken.getExpireTime(), new Date(), TimeUnit.MINUTES))
                            .build()
                    ).encode()
                );
            }))
            .err(throwable -> requestProcessingErrorHandler.handleError(throwable, ctx));
    }
}
