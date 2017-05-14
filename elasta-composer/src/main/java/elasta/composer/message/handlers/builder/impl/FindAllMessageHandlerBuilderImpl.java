package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.converter.JsonObjectToPageRequestConverter;
import elasta.composer.converter.JsonObjectToQueryParamsConverter;
import elasta.composer.flow.builder.impl.FindAllFlowBuilderImpl;
import elasta.composer.impl.ContextHolderImpl;
import elasta.composer.impl.RequestContextImpl;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.FindAllMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.PageModelBuilder;
import elasta.composer.state.handlers.UserIdConverter;
import elasta.composer.state.handlers.impl.AuthorizationStateHandlerBuilderImpl;
import elasta.composer.state.handlers.impl.EndStateHandlerBuilderImpl;
import elasta.composer.state.handlers.impl.FindAllStateHandlerBuilderImpl;
import elasta.composer.state.handlers.impl.StartStateHandlerBuilderImpl;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.orm.query.expression.FieldExpression;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 5/14/2017.
 */
final public class FindAllMessageHandlerBuilderImpl implements FindAllMessageHandlerBuilder {
    final String entity;
    final Orm orm;
    final Authorizer authorizer;
    final String action;
    final AuthorizationErrorModelBuilder authorizationErrorModelBuilder;
    final UserIdConverter userIdConverter;
    final ConvertersMap convertersMap;
    final FieldExpression paginationKey;
    final JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter;
    final JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter;
    final PageModelBuilder pageModelBuilder;

    public FindAllMessageHandlerBuilderImpl(String entity, Orm orm, Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, UserIdConverter userIdConverter, ConvertersMap convertersMap, FieldExpression paginationKey, JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter, JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter, PageModelBuilder pageModelBuilder) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
        Objects.requireNonNull(userIdConverter);
        Objects.requireNonNull(convertersMap);
        Objects.requireNonNull(paginationKey);
        Objects.requireNonNull(jsonObjectToPageRequestConverter);
        Objects.requireNonNull(jsonObjectToQueryParamsConverter);
        Objects.requireNonNull(pageModelBuilder);
        this.entity = entity;
        this.orm = orm;
        this.authorizer = authorizer;
        this.action = action;
        this.authorizationErrorModelBuilder = authorizationErrorModelBuilder;
        this.userIdConverter = userIdConverter;
        this.convertersMap = convertersMap;
        this.paginationKey = paginationKey;
        this.jsonObjectToPageRequestConverter = jsonObjectToPageRequestConverter;
        this.jsonObjectToQueryParamsConverter = jsonObjectToQueryParamsConverter;
        this.pageModelBuilder = pageModelBuilder;
    }

    @Override
    public JsonObjectMessageHandler build() {

        final ContextHolderImpl contextHolder = new ContextHolderImpl();
        final RequestContextImpl requestContext = new RequestContextImpl(contextHolder, convertersMap.getMap());

        final Flow flow = new FindAllFlowBuilderImpl(
            startHandler(),
            authorizationHandler(requestContext),
            findAllHandler(),
            endHandler()
        ).build();
        return message -> flow.start(message.body());
    }

    private EnterEventHandlerP endHandler() {
        return new EndStateHandlerBuilderImpl().build();
    }

    private EnterEventHandlerP findAllHandler() {
        return new FindAllStateHandlerBuilderImpl(
            entity,
            paginationKey,
            orm,
            jsonObjectToPageRequestConverter,
            jsonObjectToQueryParamsConverter,
            pageModelBuilder
        ).build();
    }

    private EnterEventHandlerP authorizationHandler(RequestContextImpl requestContext) {
        return new AuthorizationStateHandlerBuilderImpl(
            authorizer,
            requestContext,
            userIdConverter,
            action,
            authorizationErrorModelBuilder
        ).build();
    }

    private EnterEventHandlerP startHandler() {
        return new StartStateHandlerBuilderImpl().build();
    }
}
