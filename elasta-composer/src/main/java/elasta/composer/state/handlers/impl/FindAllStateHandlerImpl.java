package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.converter.JsonObjectToPageRequestConverter;
import elasta.composer.converter.JsonObjectToQueryParamsConverter;
import elasta.composer.model.PageRequest;
import elasta.composer.model.response.builder.PageModelBuilder;
import elasta.composer.state.handlers.FindAllStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.Orm;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class FindAllStateHandlerImpl implements FindAllStateHandler<JsonObject, JsonObject> {
    final String entity;
    final String alias;
    final Collection<FieldExpression> selections;
    final FieldExpression paginationKey;
    final Orm orm;
    final JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter;
    final JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter;
    final PageModelBuilder pageModelBuilder;

    public FindAllStateHandlerImpl(String entity, String alias, Collection<FieldExpression> selections, FieldExpression paginationKey, Orm orm, JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter, JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter, PageModelBuilder pageModelBuilder) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(alias);
        Objects.requireNonNull(selections);
        Objects.requireNonNull(paginationKey);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(jsonObjectToPageRequestConverter);
        Objects.requireNonNull(jsonObjectToQueryParamsConverter);
        Objects.requireNonNull(pageModelBuilder);
        this.entity = entity;
        this.alias = alias;
        this.selections = selections;
        this.paginationKey = paginationKey;
        this.orm = orm;
        this.jsonObjectToPageRequestConverter = jsonObjectToPageRequestConverter;
        this.jsonObjectToQueryParamsConverter = jsonObjectToQueryParamsConverter;
        this.pageModelBuilder = pageModelBuilder;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {

        JsonObject query = msg.body();

        final PageRequest pageRequest = jsonObjectToPageRequestConverter.convert(query);

        QueryExecutor.QueryParams params = jsonObjectToQueryParamsConverter.convert(
            JsonObjectToQueryParamsConverter.ConvertParams.builder()
                .entity(entity)
                .alias(alias)
                .selections(selections)
                .query(query)
                .paginationKey(paginationKey)
                .pageRequest(pageRequest)
                .build()
        );

        return Promises.when(
            orm.findAll(params),
            orm.countDistinct(
                Orm.CountDistinctParams.builder()
                    .alias(params.getAlias())
                    .entity(params.getEntity())
                    .joinParams(params.getJoinParams())
                    .criteria(params.getCriteria())
                    .groupBy(params.getGroupBy())
                    .having(params.getHaving())
                    .build()
            )
        ).map(tpl2 -> tpl2.apply((jsonObjects, count) -> Flow.trigger(Events.next, msg.withBody(
            pageModelBuilder.build(
                PageModelBuilder.BuildParams.builder()
                    .content(jsonObjects)
                    .pageRequest(pageRequest)
                    .totalElementsCount(count)
                    .build()
            )
        ))));
    }
}
