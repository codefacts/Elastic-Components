package elasta.orm.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.commons.Utils;
import elasta.orm.delete.impl.JsonDependencyHandler;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.ex.OptionalListBuilderException;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by sohan on 4/30/2017.
 */
class OptionalListBuilder {
    static final String op = "op";
    final EntityMappingHelper helper;
    final QueryExecutor.QueryParams params;

    public OptionalListBuilder(EntityMappingHelper helper, QueryExecutor.QueryParams params) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(params);
        this.helper = helper;
        this.params = params;
    }

    public Set<PathExpression> build() {
        return ImmutableSet.<PathExpression>builder()
            .addAll(optionalPaths(params.getEntity(), params.getCriteria()))
            .addAll(optionalPaths(params.getEntity(), params.getHaving()))
            .addAll(optionalPaths2(params.getEntity(), params.getGroupBy()))
            .addAll(optionalPaths3(params.getEntity(), params.getOrderBy()))
            .build();
    }

    private Collection<PathExpression> optionalPaths3(String entity, Collection<QueryExecutor.OrderTpl> orderBy) {

        return toOptionalPaths(entity, orderBy.stream().map(QueryExecutor.OrderTpl::getField));
    }

    private Collection<PathExpression> optionalPaths2(String entity, Collection<FieldExpression> fieldExpressions) {

        return toOptionalPaths(entity, fieldExpressions.stream());
    }

    private Collection<PathExpression> toOptionalPaths(String entity, Stream<FieldExpression> stream) {

        ImmutableList.Builder<PathExpression> optionalListBuilder = ImmutableList.builder();

        stream
            .filter(fieldExpression -> {

                if (Utils.not(fieldExpression.getParent().startsWith(params.getAlias()))) {
                    throw new OptionalListBuilderException("FieldExpression '" + fieldExpression + "' does not starts with root alias '" + params.getAlias() + "'");
                }

                return Utils.not(
                    helper.isMandatory(params.getEntity(), fieldExpression.getParent())
                );
            })
            .forEach(expression -> optionalListBuilder.add(expression.getParent()));

        return optionalListBuilder.build();
    }

    private List<PathExpression> optionalPaths(String entity, JsonObject criteria) {
        ImmutableList.Builder<PathExpression> listBuilder = ImmutableList.builder();

        traverseRecursive(entity, criteria, listBuilder);

        return listBuilder.build();
    }

    private void traverseRecursive(String entity, JsonObject criteria, ImmutableList.Builder<PathExpression> listBuilder) {

        final String operation = criteria.getString(op);

        if (operation != null && operation.equals("field")) {

            FieldExpressionImpl fieldExpression = new FieldExpressionImpl(
                criteria.getString("arg")
            );

            if (helper.isMandatory(entity, fieldExpression.getParent())) {
                return;
            }

            listBuilder.add(fieldExpression.getParent());
            return;
        }

        criteria.forEach(
            entry -> new JsonDependencyHandler(jsonObject -> traverseRecursive(entity, jsonObject, listBuilder))
                .handle(entry.getValue())
        );
    }
}
