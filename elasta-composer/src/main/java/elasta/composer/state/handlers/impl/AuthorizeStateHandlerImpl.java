package elasta.composer.state.handlers.impl;

import elasta.authorization.Authorizer;
import elasta.commons.Utils;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.state.handlers.AuthorizeStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class AuthorizeStateHandlerImpl implements AuthorizeStateHandler<Object, Object> {
    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;

    public AuthorizeStateHandlerImpl(Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        this.authorizer = authorizer;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
    }

    @Override
    public Promise<StateTrigger<Msg<Object>>> handle(Msg<Object> msg) throws Throwable {
        return authorizer
            .authorize(
                Authorizer.AuthorizeParams.builder()
                    .action(action)
                    .userId(msg.userId())
                    .request(msg.body())
                    .build()
            )
            .map(authorize -> {
                if (Utils.not(authorize)) {
                    return Flow.trigger(
                        Events.authorizationError,
                        msg.withBody(
                            authorizationErrorModelBuilder.build(
                                AuthorizationErrorModelBuilder.BuildParams.builder().build()
                            )
                        )
                    );
                }

                return Flow.trigger(Events.next, msg);
            });
    }
}
