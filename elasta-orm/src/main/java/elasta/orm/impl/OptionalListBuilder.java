package elasta.orm.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.commons.Utils;
import elasta.orm.delete.JoHandler;
import elasta.orm.delete.impl.JsonDependencyHandler;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.ex.OptionalListBuilderException;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by sohan on 4/30/2017.
 */
final class OptionalListBuilder {
    static final String op = "op";
    final EntityMappingHelper helper;
    final QueryExecutor.QueryParams params;
    final Map<String, PathExpression> aliasToFullPathExpressionMap;

    public OptionalListBuilder(EntityMappingHelper helper, QueryExecutor.QueryParams params, Map<String, PathExpression> aliasToFullPathExpressionMap) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(params);
        Objects.requireNonNull(aliasToFullPathExpressionMap);
        this.helper = helper;
        this.params = params;
        this.aliasToFullPathExpressionMap = aliasToFullPathExpressionMap;
    }

    public Set<PathExpression> build() {
        return ImmutableSet.<PathExpression>builder()
            .addAll(optionalPaths(params.getEntity(), params.getCriteria()))
            .addAll(optionalPaths(params.getEntity(), params.getHaving()))
            .addAll(optionalPaths2(params.getGroupBy()))
            .addAll(optionalPaths3(params.getOrderBy()))
            .build();
    }

    private Collection<PathExpression> optionalPaths3(Collection<QueryExecutor.OrderTpl> orderBy) {

        return toOptionalPaths(orderBy.stream().map(QueryExecutor.OrderTpl::getField));
    }

    private Collection<PathExpression> optionalPaths2(Collection<FieldExpression> fieldExpressions) {

        return toOptionalPaths(fieldExpressions.stream());
    }

    private Collection<PathExpression> toOptionalPaths(Stream<FieldExpression> stream) {

        ImmutableList.Builder<PathExpression> optionalListBuilder = ImmutableList.builder();

        stream
            .filter(fieldExpression -> {

                checkAliasOrThrow(fieldExpression);

                return Utils.not(
                    helper.isMandatory(
                        params.getEntity(),
                        toFullPathExp(fieldExpression.getParent())
                    )
                );
            })
            .forEach(fieldExpression -> optionalListBuilder.add(fieldExpression.getParent()));

        return optionalListBuilder.build();
    }

    private PathExpression toFullPathExp(PathExpression pathExpression) {
        if (Objects.equals(pathExpression.root(), params.getAlias())) {
            return pathExpression;
        }
        return aliasToFullPathExpressionMap.get(pathExpression.root())
            .concat(pathExpression.subPath(1, pathExpression.size()));
    }

    private void checkAliasOrThrow(FieldExpression fieldExpression) {

        final String alias = fieldExpression.getParent().root();

        boolean aliasNotFound = Utils.not(
            aliasToFullPathExpressionMap.containsKey(alias) || Objects.equals(alias, params.getAlias())
        );

        if (aliasNotFound) {
            throw new OptionalListBuilderException("FieldExpression '" + fieldExpression + "' does not starts with root alias '" + params.getAlias() + "'");
        }
    }

    private List<PathExpression> optionalPaths(String entity, JsonObject criteria) {
        ImmutableList.Builder<PathExpression> listBuilder = ImmutableList.builder();

        traverseRecursive(entity, criteria, listBuilder);

        return listBuilder.build();
    }

    private void traverseRecursive(String entity, JsonObject criteria, ImmutableList.Builder<PathExpression> listBuilder) {

        final String operation = criteria.getString(op);

        if (Objects.equals(operation, "field")) {

            FieldExpressionImpl fieldExpression = new FieldExpressionImpl(
                criteria.getString("arg")
            );

            final PathExpression pathExpression = fieldExpression.getParent();

            checkAliasOrThrow(fieldExpression);

            final boolean isMandatoy = helper.isMandatory(
                entity,
                toFullPathExp(pathExpression)
            );

            if (isMandatoy) {
                return;
            }

            listBuilder.add(pathExpression);
            return;
        }

        criteria.forEach(
            entry -> new JsonHandler(jsonObject -> traverseRecursive(entity, jsonObject, listBuilder))
                .handle(entry.getValue())
        );
    }

    final private class JsonHandler {
        final JoHandler joHandler;

        public JsonHandler(JoHandler joHandler) {
            Objects.requireNonNull(joHandler);
            this.joHandler = joHandler;
        }

        public void handle(Object value) {

            if (value instanceof JsonObject) {

                handleJo((JsonObject) value);

            } else if (value instanceof Map) {

                handleJo(new JsonObject(toMap(value)));

            } else if (value instanceof JsonArray) {

                handleJa((JsonArray) value);

            } else if (value instanceof List) {

                handleJa(new JsonArray(toList(value)));
            }
        }

        private void handleJo(JsonObject value) {
            joHandler.handle(value);
        }

        private void handleJa(JsonArray value) {
            for (int i = 0; i < value.size(); i++) {
                final Object object = value.getValue(i);
                if (object instanceof Map || object instanceof JsonObject) {
                    handleJo(value.getJsonObject(i));
                }
            }
        }

        private List toList(Object value) {
            return (List) value;
        }

        private Map<String, Object> toMap(Object value) {
            return (Map<String, Object>) value;
        }
    }
}
