package elasta.composer.state.handlers.impl;

import elasta.authorization.Authorizer;
import elasta.commons.Utils;
import elasta.composer.Events;
import elasta.composer.Headers;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.request.UserModel;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.state.handlers.AuthorizationStateHandlerBuilder;
import elasta.composer.state.handlers.UserIdConverter;
import elasta.composer.state.handlers.ex.UserIdNotFoundException;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class AuthorizationStateHandlerBuilderImpl implements AuthorizationStateHandlerBuilder {
    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;

    public AuthorizationStateHandlerBuilderImpl(Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        this.authorizer = authorizer;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
    }

    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {
        return
            request ->
                authorizer
                    .authorize(
                        Authorizer.AuthorizeParams.builder()
                            .action(action)
                            .userId(request.userId())
                            .request(request.body())
                            .build()
                    )
                    .map(authorize -> {
                        if (Utils.not(authorize)) {
                            return Flow.trigger(
                                Events.authorizationError,
                                request.withBody(
                                    authorizationErrorModelBuilder.build(
                                        AuthorizationErrorModelBuilder.BuildParams.builder().build()
                                    )
                                )
                            );
                        }

                        return Flow.trigger(Events.next, request);
                    });
    }
}
