package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.response.builder.PageModelBuilder;
import elasta.composer.converter.JsonObjectToPageRequestConverter;
import elasta.composer.converter.JsonObjectToQueryParamsConverter;
import elasta.composer.model.PageRequest;
import elasta.composer.state.handlers.FindAllStateHandlerBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.orm.Orm;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonObject;

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

    public FindAllStateHandlerBuilderImpl(String entity, FieldExpression paginationKey, Orm orm, JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter, JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter, PageModelBuilder pageModelBuilder) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(paginationKey);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(jsonObjectToPageRequestConverter);
        Objects.requireNonNull(jsonObjectToQueryParamsConverter);
        Objects.requireNonNull(pageModelBuilder);
        this.entity = entity;
        this.paginationKey = paginationKey;
        this.orm = orm;
        this.jsonObjectToPageRequestConverter = jsonObjectToPageRequestConverter;
        this.jsonObjectToQueryParamsConverter = jsonObjectToQueryParamsConverter;
        this.pageModelBuilder = pageModelBuilder;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return msg -> {

            JsonObject query = msg.body();

            final PageRequest pageRequest = jsonObjectToPageRequestConverter.convert(query);

            QueryExecutor.QueryParams params = jsonObjectToQueryParamsConverter.convert(
                JsonObjectToQueryParamsConverter.ConvertParams.builder()
                    .entity(entity)
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
        };
    }
}
