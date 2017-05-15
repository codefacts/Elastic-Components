package elasta.composer.message.handlers.builder.impl;

import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.JsonObjectToPageRequestConverter;
import elasta.composer.converter.JsonObjectToQueryParamsConverter;
import elasta.composer.flow.builder.impl.FindAllFlowBuilderImpl;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.FindAllMessageHandlerBuilder;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.PageModelBuilder;
import elasta.composer.state.handlers.UserIdConverter;
import elasta.composer.state.handlers.impl.AuthorizationStateHandlerBuilderImpl;
import elasta.composer.state.handlers.impl.EndStateHandlerBuilderImpl;
import elasta.composer.state.handlers.impl.FindAllStateHandlerBuilderImpl;
import elasta.composer.state.handlers.impl.StartStateHandlerBuilderImpl;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.orm.query.expression.FieldExpression;

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
    final ConvertersMap convertersMap;
    final FieldExpression paginationKey;
    final JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter;
    final JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter;
    final PageModelBuilder pageModelBuilder;

    public FindAllMessageHandlerBuilderImpl(String entity, Orm orm, Authorizer authorizer, String action, AuthorizationErrorModelBuilder authorizationErrorModelBuilder, ConvertersMap convertersMap, FieldExpression paginationKey, JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter, JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter, PageModelBuilder pageModelBuilder) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(authorizer);
        Objects.requireNonNull(action);
        Objects.requireNonNull(authorizationErrorModelBuilder);
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
        this.convertersMap = convertersMap;
        this.paginationKey = paginationKey;
        this.jsonObjectToPageRequestConverter = jsonObjectToPageRequestConverter;
        this.jsonObjectToQueryParamsConverter = jsonObjectToQueryParamsConverter;
        this.pageModelBuilder = pageModelBuilder;
    }

    @Override
    public JsonObjectMessageHandler build() {

        final Flow flow = new FindAllFlowBuilderImpl(
            startHandler(),
            authorizationHandler(),
            findAllHandler(),
            endHandler()
        ).build();
        return message -> flow.start(message.body());
    }

    private MsgEnterEventHandlerP endHandler() {
        return new EndStateHandlerBuilderImpl().build();
    }

    private MsgEnterEventHandlerP findAllHandler() {
        return new FindAllStateHandlerBuilderImpl(
            entity,
            paginationKey,
            orm,
            jsonObjectToPageRequestConverter,
            jsonObjectToQueryParamsConverter,
            pageModelBuilder
        ).build();
    }

    private MsgEnterEventHandlerP authorizationHandler() {
        return new AuthorizationStateHandlerBuilderImpl(
            authorizer,
            action,
            authorizationErrorModelBuilder
        ).build();
    }

    private MsgEnterEventHandlerP startHandler() {
        return new StartStateHandlerBuilderImpl().build();
    }
}
