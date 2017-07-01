package elasta.orm.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.commons.Utils;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.core.touple.immutable.Tpls;
import elasta.orm.BaseOrm;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.ex.QueryDataLoaderException;
import elasta.orm.impl.QueryDataLoaderImpl.OptionalData;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.JsonOps;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by sohan on 4/30/2017.
 */
final class DataLoader {
    final BaseOrm baseOrm;
    final EntityMappingHelper helper;

    DataLoader(BaseOrm baseOrm, EntityMappingHelper helper) {
        Objects.requireNonNull(baseOrm);
        Objects.requireNonNull(helper);
        this.baseOrm = baseOrm;
        this.helper = helper;
    }

    Promise<JsonObject> loadRecursive(JsonObject jsonObject, Collection<OptionalData> values) {

        return Promises
            .when(
                values.stream()
                    .map(
                        optionalData -> reloadJsonObject(
                            jsonObject,
                            optionalData.getEntity(),
                            optionalData.getPathExpression(),
                            optionalData.getFields(),
                            ImmutableSet.of()
                        ).mapP(optionalJsonObject -> {

                            if (Utils.not(optionalJsonObject.isPresent())) {
                                return Promises.of(
                                    Optional.<JsonObject>empty()
                                );
                            }
                            return loadDataRecursive(optionalJsonObject.get(), optionalData.getPathExpression(), optionalData.getPathExpressionToOptionalDataMap().values())
                                .map(Optional::of);
                        })
                    )
                    .collect(Collectors.toList())
            )
            .map(
                ts -> ts.stream()
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .reduce(new JsonObject(), (jsonObject1, jsonObject2) -> {
                        jsonObject1.getMap().putAll(jsonObject2.getMap());
                        return jsonObject1;
                    })
            );
    }

    private Promise<JsonObject> loadDataRecursive(JsonObject jsonObject, PathExpression pathExpression, Collection<OptionalData> optionalDatas) {

        if (pathExpression.size() < 2) {
            throw new QueryDataLoaderException("PathExpression '" + pathExpression + "' size '" + pathExpression.size() + "' can not be less than 2");
        }

        return Promises
            .when(
                getJsonObjectList(jsonObject, pathExpression.subPath(1, pathExpression.size()))
                    .map(js -> loadRecursive(js, optionalDatas).map(jsObj -> Tpls.of(js, jsObj)))
                    .collect(Collectors.toList())
            )
            .then(tpl2s -> tpl2s.forEach(tpl2 -> tpl2.accept((jsonObject1, jsonObject2) -> jsonObject1.getMap().putAll(jsonObject2.getMap()))))
            .map(tpl2s -> jsonObject)
            ;
    }

    private Stream<JsonObject> getJsonObjectList(JsonObject jsonObject, PathExpression pathExpression) {

        Object value = jsonObject.getValue(pathExpression.root());

        if (pathExpression.size() == 1) {
            return toJoList(value);
        }

        return toJoListRecursive(value, pathExpression.subPath(1, pathExpression.size()));
    }

    private Stream<JsonObject> toJoListRecursive(Object value, PathExpression subPath) {

        if (value instanceof Map) {

            return getJsonObjectList(
                new JsonObject(castMap(value)),
                subPath
            );

        } else if (value instanceof JsonObject) {

            return getJsonObjectList(
                (JsonObject) value,
                subPath
            );

        } else if (value instanceof List) {

            return castList(value)
                .stream()
                .flatMap(js -> getJsonObjectList(js, subPath))
                ;

        } else if (value instanceof JsonArray) {

            return castList(
                ((JsonArray) value).getList())
                .stream()
                .flatMap(js -> getJsonObjectList(js, subPath))
                ;
        }

        throw new QueryDataLoaderException("value '" + value + "' must be JsonObject or JsonArray");
    }

    private Stream<JsonObject> toJoList(Object value) {
        if (value instanceof Map) {
            return Stream.of(
                new JsonObject(castMap(value))
            );
        } else if (value instanceof JsonObject) {
            return Stream.of(
                (JsonObject) value
            );
        } else if (value instanceof List) {

            return castList(value).stream();

        } else if (value instanceof JsonArray) {

            return castList(((JsonArray) value).getList()).stream();
        }
        throw new QueryDataLoaderException("value '" + value + "' must be JsonObject or JsonArray");
    }

    private List<JsonObject> castList(Object value) {
        return (List<JsonObject>) value;
    }

    private Map<String, Object> castMap(Object value) {
        return (Map<String, Object>) value;
    }

    Promise<Optional<JsonObject>> reloadJsonObject(JsonObject jsonObject, String entity, PathExpression pathExpression, Set<String> fields, Set<FieldExpression> fieldExpressions) {

        final String primaryKey = helper.getPrimaryKey(entity);

        return baseOrm
            .query(
                QueryExecutor.QueryParams.builder()
                    .entity(entity)
                    .alias(pathExpression.root())
                    .joinParams(ImmutableList.of())
                    .selections(
                        ImmutableSet.<FieldExpression>builder()
                            .addAll(
                                fields.stream()
                                    .map(
                                        field -> new FieldExpressionImpl(
                                            pathExpression.concat(field)
                                        )
                                    )
                                    .collect(Collectors.toList())
                            )
                            .addAll(fieldExpressions)
                            .build()
                    )
                    .criteria(
                        JsonOps.eq(
                            (pathExpression.root() + "." + primaryKey),
                            jsonObject.getValue(primaryKey)
                        )
                    )
                    .having(new JsonObject())
                    .groupBy(ImmutableList.of())
                    .orderBy(ImmutableList.of())
                    .build()
            )
            .map(jsonObjects -> {
                if (jsonObjects.size() > 1) {
                    throw new QueryDataLoaderException("Multiple result found for primary key '" + primaryKey + "' expected only one result");
                }
                if (jsonObjects.size() <= 0) {
                    return Optional.empty();
                }
                return Optional.of(jsonObjects.get(0));
            });
    }
}
