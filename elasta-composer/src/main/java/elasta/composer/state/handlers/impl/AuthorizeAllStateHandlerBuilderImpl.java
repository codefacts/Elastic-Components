package elasta.composer.state.handlers.impl;

import elasta.authorization.Authorizer;
import elasta.commons.Utils;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.state.handlers.AuthorizeAllStateHandlerBuilder;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/20/2017.
 */
final public class AuthorizeAllStateHandlerBuilderImpl implements AuthorizeAllStateHandlerBuilder {
    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;

    public AuthorizeAllStateHandlerBuilderImpl(Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder) {
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
            msg ->
                authorizer
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
