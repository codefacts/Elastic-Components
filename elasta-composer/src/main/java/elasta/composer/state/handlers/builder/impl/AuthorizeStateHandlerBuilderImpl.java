package elasta.composer.state.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.commons.Utils;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.state.handlers.builder.AuthorizeStateHandlerBuilder;
import elasta.composer.state.handlers.impl.AuthorizeAllStateHandlerImpl;
import elasta.composer.state.handlers.impl.AuthorizeStateHandlerImpl;
import elasta.core.flow.Flow;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class AuthorizeStateHandlerBuilderImpl implements AuthorizeStateHandlerBuilder {
    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;

    public AuthorizeStateHandlerBuilderImpl(Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        this.authorizer = authorizer;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
    }

    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {
        return new AuthorizeStateHandlerImpl(
            authorizer, action, authorizationErrorModelBuilder
        );
    }
}
