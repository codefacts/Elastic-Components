package elasta.orm.impl;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import elasta.commons.Utils;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.BaseOrm;
import elasta.orm.QueryDataLoader;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.ex.QueryDataLoaderException;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 4/20/2017.
 */
final public class QueryDataLoaderImpl implements QueryDataLoader {
    final BaseOrm baseOrm;
    final EntityMappingHelper helper;

    public QueryDataLoaderImpl(BaseOrm baseOrm, EntityMappingHelper helper) {
        Objects.requireNonNull(baseOrm);
        Objects.requireNonNull(helper);
        this.baseOrm = baseOrm;
        this.helper = helper;
    }

    @Override
    public Promise<List<JsonObject>> findAll(QueryExecutor.QueryParams params) {

        final Set<PathExpression> optionalPaths = new OptionalListBuilder(helper, params).build();

        ImmutableSet.Builder<FieldExpression> mandatoryListBuilder = ImmutableSet.builder();
        ImmutableSortedSet.Builder<FieldExpression> optionalListBuilder = ImmutableSortedSet.orderedBy((o1, o2) -> {

            if (o1.equals(o2)) {
                return 0;
            }

            return o1.size() > o2.size() ? 1 : -1;
        });

        Map<String, Map<String, Map<String, Object>>> map = toOptionalPathsMaps(optionalPaths);

        params.getSelections().forEach(fieldExpression -> {

            if (Utils.not(fieldExpression.getParent().startsWith(params.getAlias()))) {
                throw new QueryDataLoaderException("FieldExpression '" + fieldExpression + "' does not starts with rootAlias '" + params.getAlias() + "'");
            }

            if (helper.isMandatory(params.getEntity(), fieldExpression.getParent())) {

                mandatoryListBuilder.add(fieldExpression);

            } else if (startsWithInOptionalPaths(map, fieldExpression)) {

                mandatoryListBuilder.add(fieldExpression);

            } else {

                optionalListBuilder.add(fieldExpression);
            }
        });

        final Set<FieldExpression> mandatoryFieldExpressions = mandatoryListBuilder.build();
        final Set<FieldExpression> optionalFieldExpressions = optionalListBuilder.build();

        return baseOrm
            .query(
                QueryExecutor.QueryParams.builder()
                    .entity(params.getEntity())
                    .alias(params.getAlias())
                    .selections(mandatoryFieldExpressions)
                    .joinParams(params.getJoinParams())
                    .criteria(params.getCriteria())
                    .having(params.getHaving())
                    .groupBy(params.getGroupBy())
                    .orderBy(params.getOrderBy())
                    .build()
            )
            .mapP(
                jsonObjects -> {

                    final Map<PathExpression, OptionalData> dataMap = new OptionalDataMapBuilder(params.getEntity(), optionalFieldExpressions, helper).build();

                    return Promises.when(
                        jsonObjects.stream()
                            .map(
                                jsonObject -> loadData(
                                    jsonObject,
                                    dataMap
                                )
                            ).collect(Collectors.toList())
                    );
                }
            )
            ;
    }

    private Promise<JsonObject> loadData(JsonObject jsonObject, Map<PathExpression, OptionalData> dataMap) {

        return new DataLoader(baseOrm, helper)
            .loadRecursive(jsonObject, dataMap.values())
            .map(jsObj -> combine(jsonObject, jsObj));
    }

    private JsonObject combine(JsonObject jsonObject1, JsonObject jsonObject2) {

        jsonObject1.getMap().putAll(jsonObject2.getMap());

        return jsonObject1;
    }

    private boolean startsWithInOptionalPaths(final Map<String, Map<String, Map<String, Object>>> map, FieldExpression fieldExpression) {

        Map<String, Map<String, Map<String, Object>>> m = map;

        for (String part : fieldExpression.getParent().parts()) {
            Map<String, Map<String, Object>> mp = m.get(part);
            if (mp == null) {
                return false;
            }
            m = cast(mp);
        }

        return true;
    }

    private Map<String, Map<String, Map<String, Object>>> toOptionalPathsMaps(final Set<PathExpression> optionalPaths) {
        final HashMap<String, Map<String, Map<String, Object>>> map = new HashMap<>();

        optionalPaths.forEach(pathExpression -> {

            Map<String, Map<String, Map<String, Object>>> m = map;

            for (String part : pathExpression.parts()) {
                Map<String, Map<String, Object>> mp = m.get(part);

                if (mp == null) {
                    m.put(part, mp = new HashMap<>());
                }

                m = cast(mp);
            }

        });

        return map;
    }

    private Map<String, Map<String, Map<String, Object>>> cast(Map mp) {
        return mp;
    }

    @Value
    static final class OptionalData {
        final String entity;
        final PathExpression pathExpression;
        final Set<String> fields;
        final Map<PathExpression, OptionalData> pathExpressionToOptionalDataMap;

        OptionalData(String entity, PathExpression pathExpression, Set<String> fields, Map<PathExpression, OptionalData> pathExpressionToOptionalDataMap) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(pathExpression);
            Objects.requireNonNull(fields);
            Objects.requireNonNull(pathExpressionToOptionalDataMap);
            this.entity = entity;
            this.pathExpression = pathExpression;
            this.fields = fields;
            this.pathExpressionToOptionalDataMap = pathExpressionToOptionalDataMap;
        }
    }
}
