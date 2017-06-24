package elasta.composer.state.handlers.impl;

import elasta.composer.ComposerUtils;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.converter.JsonObjectToPageRequestConverter;
import elasta.composer.converter.JsonObjectToQueryParamsConverter;
import elasta.composer.model.PageRequest;
import elasta.composer.model.response.builder.PageModelBuilder;
import elasta.composer.state.handlers.QueryAllChildFindAllStateHandlerBuilder;
import elasta.composer.state.handlers.ex.MultipleResultException;
import elasta.composer.state.handlers.ex.NoResultFoundException;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.orm.Orm;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/29/2017.
 */
final public class QueryAllChildFindAllStateHandlerBuilderImpl implements QueryAllChildFindAllStateHandlerBuilder {
    final String parentEntity;
    final String parentToChildPath;
    final FieldExpression paginationKey;
    final Orm orm;
    final JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter;
    final JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter;
    final PageModelBuilder pageModelBuilder;

    public QueryAllChildFindAllStateHandlerBuilderImpl(String parentEntity, String parentToChildPath, FieldExpression paginationKey, Orm orm, JsonObjectToPageRequestConverter jsonObjectToPageRequestConverter, JsonObjectToQueryParamsConverter jsonObjectToQueryParamsConverter, PageModelBuilder pageModelBuilder) {
        Objects.requireNonNull(parentEntity);
        Objects.requireNonNull(parentToChildPath);
        Objects.requireNonNull(paginationKey);
        Objects.requireNonNull(orm);
        Objects.requireNonNull(jsonObjectToPageRequestConverter);
        Objects.requireNonNull(jsonObjectToQueryParamsConverter);
        Objects.requireNonNull(pageModelBuilder);
        this.parentEntity = parentEntity;
        this.parentToChildPath = parentToChildPath;
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

            Objects.requireNonNull(query);

            final PageRequest pageRequest = jsonObjectToPageRequestConverter.convert(query);

            QueryExecutor.QueryParams params = jsonObjectToQueryParamsConverter.convert(
                JsonObjectToQueryParamsConverter.ConvertParams.builder()
                    .entity(parentEntity)
                    .query(query)
                    .paginationKey(paginationKey)
                    .pageRequest(pageRequest)
                    .build()
            );

            return Promises.when(
                orm.findAll(params),
                orm.countDistinct(
                    Orm.CountDistinctParams.builder()
                        .countingKey(paginationKey)
                        .alias(params.getAlias())
                        .entity(params.getEntity())
                        .joinParams(params.getJoinParams())
                        .criteria(params.getCriteria())
                        .groupBy(params.getGroupBy())
                        .having(params.getHaving())
                        .build()
                )
            ).map(tpl2 -> tpl2.apply((jsonObjects, count) -> {

                if (jsonObjects.size() > 1) {
                    throw new MultipleResultException();
                }

                if (jsonObjects.size() < 1) {
                    throw new NoResultFoundException();
                }

                final JsonArray jsonArray = jsonObjects.get(0).getJsonArray(parentToChildPath, ComposerUtils.emptyJsonArray());

                return Flow.trigger(Events.next, msg.withBody(
                    pageModelBuilder.build(
                        PageModelBuilder.BuildParams.builder()
                            .content(jsonArray.getList())
                            .pageRequest(pageRequest)
                            .totalElementsCount(count)
                            .build()
                    )
                ));
            }));
        };
    }
}
