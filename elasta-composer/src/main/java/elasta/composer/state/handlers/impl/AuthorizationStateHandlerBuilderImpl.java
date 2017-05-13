package elasta.composer.state.handlers.impl;

import elasta.authorization.Authorizer;
import elasta.commons.Utils;
import elasta.composer.Events;
import elasta.composer.RequestContext;
import elasta.composer.model.request.UserModel;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.state.handlers.AuthorizationStateHandlerBuilder;
import elasta.composer.state.handlers.UserIdConverter;
import elasta.composer.state.handlers.ex.UserIdNotFoundException;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class AuthorizationStateHandlerBuilderImpl implements AuthorizationStateHandlerBuilder {
    final Authorizer authorizer;
    final RequestContext requestContext;
    final UserIdConverter userIdConverter;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;

    public AuthorizationStateHandlerBuilderImpl(Authorizer authorizer, RequestContext requestContext, UserIdConverter userIdConverter, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder) {
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(requestContext);
        Objects.requireNonNull(userIdConverter);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        this.authorizer = authorizer;
        this.requestContext = requestContext;
        this.userIdConverter = userIdConverter;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
    }

    @Override
    public EnterEventHandlerP build() {
        return request -> {

            final boolean authorize = authorizer.authorize(
                Authorizer.AuthorizeParams.builder()
                    .action(action)
                    .userId(userId())
                    .request(request)
                    .build()
            );

            if (Utils.not(authorize)) {
                return Promises.of(
                    Flow.trigger(
                        Events.authorizationError,
                        authorizationErrorModelBuilder.build(
                            AuthorizationErrorModelBuilder.BuildParams.builder().build()
                        )
                    )
                );
            }

            return Promises.of(
                Flow.trigger(Events.next, request)
            );
        };
    }

    private Object userId() {
        return userIdConverter.convert(
            requestContext.getString(UserModel.userId).orElseThrow(() -> new UserIdNotFoundException("User id does not exists in context"))
        );
    }
}
