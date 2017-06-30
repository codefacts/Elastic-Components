package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.authorization.Authorizer;
import elasta.commons.Utils;
import elasta.composer.ComposerCnsts;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.AuthorizationSuccessModelBuilder;
import elasta.composer.state.handlers.AuthorizeAllStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/30/2017.
 */
final public class AuthorizeAllStateHandlerImpl implements AuthorizeAllStateHandler<JsonArray, Json> {

    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder;

    public AuthorizeAllStateHandlerImpl(Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, AuthorizationSuccessModelBuilder authorizationSuccessModelBuilder) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(authorizationSuccessModelBuilder);
        this.authorizer = authorizer;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.authorizationSuccessModelBuilder = authorizationSuccessModelBuilder;
    }

    @Override
    public Promise<StateTrigger<Msg<Json>>> handle(Msg<JsonArray> msg) throws Throwable {
        List list = msg.body().stream()
            .map(
                obj -> authorizer
                    .authorize(
                        Authorizer.AuthorizeParams.builder()
                            .action(action)
                            .userId(msg.userId())
                            .request(obj)
                            .build()
                    )
            )
            .collect(Collectors.toList());

        return authorize(list, msg);
    }

    private Promise<StateTrigger<Msg>> authorize(List<Promise<Boolean>> list, Msg<JsonArray> msg) {
        return Promises
            .when(list)
            .map(tList -> {

                ImmutableList.Builder<JsonObject> builder = ImmutableList.builder();

                boolean pass = true;

                for (boolean authorizeOk : tList) {

                    if (authorizeOk) {

                        builder.add(
                            authorizationSuccessModelBuilder.build(
                                AuthorizationSuccessModelBuilder.BuildParams.builder().build()
                            )
                        );

                    } else {

                        builder.add(
                            authorizationErrorModelBuilder.build(
                                AuthorizationErrorModelBuilder.BuildParams.builder().build()
                            )
                        );

                        pass = false;
                    }
                }

                if (Utils.not(pass)) {

                    return Flow.trigger(
                        Events.authorizationError,
                        msg.withBody(
                            new JsonObject(
                                ImmutableMap.<String, Object>builder()
                                    .putAll(
                                        authorizationErrorModelBuilder.build(
                                            AuthorizationErrorModelBuilder.BuildParams.builder().build()
                                        )
                                    )
                                    .put(ComposerCnsts.data, builder.build())
                                    .build()
                            )
                        )
                    );
                }

                return Flow.trigger(
                    Events.next,
                    msg
                );

            })
            ;
    }
}
