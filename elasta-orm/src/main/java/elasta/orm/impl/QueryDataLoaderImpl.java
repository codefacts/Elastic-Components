package elasta.orm.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.core.touple.immutable.Tpl3;
import elasta.core.touple.immutable.Tpl4;
import elasta.core.touple.immutable.Tpls;
import elasta.orm.BaseOrm;
import elasta.orm.QueryDataLoader;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.ex.QueryDataLoaderException;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.builder.impl.PathExpressionAndAliasPair;
import elasta.orm.query.expression.impl.AliasToFullPathExpressionBuilder;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public Promise<List<JsonObject>> query(QueryExecutor.QueryParams params) {

        Map<String, PathExpression> aliasToFullPathExpressionMap = new AliasToFullPathExpressionBuilder(
            params.getAlias(), params.getEntity(),
            params.getJoinParams().stream().map(joinParam -> new PathExpressionAndAliasPair(joinParam.getPath(), joinParam.getAlias(), joinParam.getJoinType()))
        ).build();

        MandatoryAndOptionalFields fields = new MandatoryAndOptionalFieldsBuilder(helper, aliasToFullPathExpressionMap).build(params);

        return baseOrm
            .query(
                QueryExecutor.QueryParams.builder()
                    .entity(params.getEntity())
                    .alias(params.getAlias())
                    .selections(fields.getMandatoryFieldExpressions())
                    .joinParams(params.getJoinParams())
                    .criteria(params.getCriteria())
                    .having(params.getHaving())
                    .groupBy(params.getGroupBy())
                    .orderBy(params.getOrderBy())
                    .build()
            )
            .mapP(
                jsonObjects -> {

                    final Map<String, Map<PathExpression, OptionalData>> rootAliasToDataMap = new OptionalDataMapBuilder(
                        params.getEntity(),
                        params.getAlias(),
                        fields.getOptionalFieldExpressions(),
                        aliasToFullPathExpressionMap,
                        helper
                    ).build();

                    return Promises.when(
                        jsonObjects.stream()
                            .map(
                                rootEntity -> loadJsonObject(rootEntity, fields.getAliasToFieldsMap(), rootAliasToDataMap, aliasToFullPathExpressionMap, params.getAlias(), params.getEntity())
                            )
                            .collect(Collectors.toList())
                    );
                }
            )
            ;
    }

    private Promise<JsonObject> loadJsonObject(final JsonObject rootJsonObject, SetMultimap<String, String> aliasToFieldsMap, Map<String, Map<PathExpression, OptionalData>> rootAliasToDataMap, Map<String, PathExpression> aliasToFullPathExpressionMap, String rootAlias, String rootEntity) {

        final DataLoader dataLoader = new DataLoader(baseOrm, helper);

        return Promises
            .when(
                rootAliasToDataMap.entrySet().stream()
                    .filter(e -> e.getValue().size() > 0)
                    .map(
                        e -> Tpls.of(
                            toPathExp(e.getKey(), aliasToFullPathExpressionMap, rootAlias),
                            e.getValue(),
                            e.getKey()
                        )
                    )
                    .map(tpl2 -> tpl2.apply((pathExpression, pathExpressionOptionalDataMap, alias) -> {

                        if (Objects.equals(alias, rootAlias)) {
                            return Promises.of(Tpls.of(
                                Optional.of(rootJsonObject), pathExpression, pathExpressionOptionalDataMap, alias
                            ));
                        }

                        return dataLoader
                            .reloadJsonObject(
                                rootJsonObject, rootEntity, pathExpression,
                                aliasToFieldsMap.get(alias),
                                dependencyFields(rootEntity, aliasToFullPathExpressionMap.get(alias))
                            )
                            .map(jsonObjectOptional -> Tpls.of(jsonObjectOptional, pathExpression, pathExpressionOptionalDataMap, alias))
                            ;
                    }))
                    .collect(Collectors.toList())
            )
            .mapP(tpl3s -> {

                List<Promise<JsonObject>> promiseList = tpl3s.stream()
                    .filter(tpl3 -> tpl3.t1.isPresent())
                    .peek(tpl3 -> tpl3.accept((jsonObjectOptional, pathExpression, pathExpressionOptionalDataMap, alias) -> combine(rootJsonObject, jsonObjectOptional.get())))
                    .flatMap(tpl3 -> tpl3.apply((jsonObjectOptional, pathExpression, pathExpressionOptionalDataMap, alias) -> {

                        if (Objects.equals(alias, rootAlias)) {
                            return Stream.of(
                                loadData(rootJsonObject, pathExpressionOptionalDataMap)
                            );
                        }

                        List<JsonObject> jsonObjectList = accessList(jsonObjectOptional.get(), pathExpression.subPath(1));

                        return jsonObjectList.stream()
                            .map(jo -> loadData(jo, pathExpressionOptionalDataMap));
                    }))
                    .collect(Collectors.toList());

                return Promises.when(
                    promiseList
                ).map(jsonObjects -> rootJsonObject);
            })
            ;
    }

    private Set<FieldExpression> dependencyFields(String rootEntity, PathExpression pathExpression) {
        final String rootAlias = pathExpression.root();
        ImmutableSet.Builder<FieldExpression> pathExpSetBuilder = ImmutableSet.builder();

        pathExpSetBuilder.add(
            new FieldExpressionImpl(
                PathExpression.create(rootAlias, helper.getPrimaryKey(rootEntity))
            )
        );

        for (int i = 2, size = pathExpression.size(); i <= size; i++) {
            final PathExpression subPath = pathExpression.subPath(0, i);
            pathExpSetBuilder.add(
                new FieldExpressionImpl(
                    subPath.concat(
                        PathExpression.create(
                            helper.getPrimaryKey(
                                helper.getReferencingEntity(rootEntity, new FieldExpressionImpl(subPath))
                            )
                        )
                    )
                )
            );
        }

        return pathExpSetBuilder.build();
    }

    private List<JsonObject> accessList(JsonObject jo, PathExpression pathExpression) {
        List<String> parts = pathExpression.parts();
        for (int index = 0, end = parts.size() - 1; index < end; index++) {
            jo = jo.getJsonObject(parts.get(index));
            if (jo == null) {
                return ImmutableList.of();
            }
        }

        final JsonArray jsonArray = jo.getJsonArray(pathExpression.last());

        if (jsonArray == null) {
            return ImmutableList.of();
        }

        return jsonArray.getList();
    }

    private PathExpression toPathExp(String alias, Map<String, PathExpression> aliasToFullPathExpressionMap, String rootAlias) {

        if (Objects.equals(alias, rootAlias)) {
            return PathExpression.create(rootAlias);
        }

        return aliasToFullPathExpressionMap.get(alias);
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
            if (pathExpression.size() < 2) {
                throw new QueryDataLoaderException("PathExp '" + pathExpression + "' size '" + pathExpression.size() + "' can not be less than 2");
            }
            this.entity = entity;
            this.pathExpression = pathExpression;
            this.fields = fields;
            this.pathExpressionToOptionalDataMap = pathExpressionToOptionalDataMap;
        }
    }

    @Value
    static final class MandatoryAndOptionalFields {
        final Set<FieldExpression> mandatoryFieldExpressions;
        final Set<FieldExpression> optionalFieldExpressions;
        final SetMultimap<String, String> aliasToFieldsMap;

        MandatoryAndOptionalFields(Set<FieldExpression> mandatoryFieldExpressions, Set<FieldExpression> optionalFieldExpressions, SetMultimap<String, String> aliasToFieldsMap) {
            Objects.requireNonNull(mandatoryFieldExpressions);
            Objects.requireNonNull(optionalFieldExpressions);
            Objects.requireNonNull(aliasToFieldsMap);
            this.mandatoryFieldExpressions = mandatoryFieldExpressions;
            this.optionalFieldExpressions = optionalFieldExpressions;
            this.aliasToFieldsMap = aliasToFieldsMap;
        }
    }

    @Value
    private static final class JsonObjectAndDataMap {
        private final JsonObject jsonObject;
        private final Map<PathExpression, OptionalData> dataMap;

        public JsonObjectAndDataMap(JsonObject jsonObject, Map<PathExpression, OptionalData> dataMap) {
            Objects.requireNonNull(jsonObject);
            Objects.requireNonNull(dataMap);
            this.jsonObject = jsonObject;
            this.dataMap = dataMap;
        }
    }
}
