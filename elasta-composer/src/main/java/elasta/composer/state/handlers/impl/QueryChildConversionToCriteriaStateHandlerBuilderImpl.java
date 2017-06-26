package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.request.QueryChildModel;
import elasta.composer.state.handlers.QueryChildConversionToCriteriaStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.sql.SqlOps;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/28/2017.
 */
final public class QueryChildConversionToCriteriaStateHandlerBuilderImpl implements QueryChildConversionToCriteriaStateHandlerBuilder {
    final String parentPrimaryKeyExpStr;
    final String parentToChildPathExpStr;

    public QueryChildConversionToCriteriaStateHandlerBuilderImpl(String parentPrimaryKeyExpStr, String parentToChildPathExpStr) {
        Objects.requireNonNull(parentPrimaryKeyExpStr);
        Objects.requireNonNull(parentToChildPathExpStr);
        this.parentPrimaryKeyExpStr = parentPrimaryKeyExpStr;
        this.parentToChildPathExpStr = parentToChildPathExpStr;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return msg -> {

            final JsonObject body = msg.body();

            Objects.requireNonNull(body);

            final JsonObject queryReq = body.getJsonObject(QueryChildModel.criteria);

            Objects.requireNonNull(queryReq);

            final JsonObject criteria = toCriteria(
                queryReq
            );

            final Object parentId = body.getValue(QueryChildModel.parentId);

            Objects.requireNonNull(parentId);

            return Promises.of(
                Flow.trigger(Events.next, msg.withBody(
                    SqlOps.and(
                        ImmutableList.of(
                            SqlOps.eq(parentPrimaryKeyExpStr, parentId),
                            criteria
                        )
                    )
                ))
            );
        };
    }

    private JsonObject toCriteria(JsonObject criteria) {

        ImmutableList.Builder<JsonObject> criteriaListBuilder = ImmutableList.builder();

        criteria.getMap().forEach((fieldName, value) -> {
            criteriaListBuilder.add(
                SqlOps.eq(parentToChildPathExpStr + "." + fieldName, value)
            );
        });

        return SqlOps.and(criteriaListBuilder.build());
    }
}
