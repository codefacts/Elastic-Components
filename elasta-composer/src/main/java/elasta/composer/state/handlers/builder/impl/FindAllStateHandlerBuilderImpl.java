package elasta.composer.state.handlers.builder.impl;

import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.response.builder.PageModelBuilder;
import elasta.composer.converter.JsonObjectToPageRequestConverter;
import elasta.composer.converter.JsonObjectToQueryParamsConverter;
import elasta.composer.state.handlers.builder.FindAllStateHandlerBuilder;
import elasta.composer.state.handlers.impl.FindAllStateHandlerImpl;
import elasta.orm.Orm;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class FindAllStateHandlerBuilderImpl implements FindAllStateHandlerBuilder {
    final String entity;
    final FieldExpression paginationKey;
    final Orm orm;
    final JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter;
    final JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter;
    final PageModelBuilder pageModelBuilder;
    final String alias;
    final Collection<FieldExpression> selections;

    public FindAllStateHandlerBuilderImpl(String entity, FieldExpression paginationKey, Orm orm, JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter, JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter, PageModelBuilder pageModelBuilder, String alias, Collection<FieldExpression> selections) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(paginationKey);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(jsonObjectToPageRequestConverter);
        Objects.requireNonNull(jsonObjectToQueryParamsConverter);
        Objects.requireNonNull(pageModelBuilder);
        Objects.requireNonNull(alias);
        Objects.requireNonNull(selections);
        this.entity = entity;
        this.paginationKey = paginationKey;
        this.orm = orm;
        this.jsonObjectToPageRequestConverter = jsonObjectToPageRequestConverter;
        this.jsonObjectToQueryParamsConverter = jsonObjectToQueryParamsConverter;
        this.pageModelBuilder = pageModelBuilder;
        this.alias = alias;
        this.selections = selections;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return new FindAllStateHandlerImpl(
            entity,
            alias,
            selections,
            paginationKey,
            orm,
            jsonObjectToPageRequestConverter,
            jsonObjectToQueryParamsConverter,
            pageModelBuilder
        );
    }
}
