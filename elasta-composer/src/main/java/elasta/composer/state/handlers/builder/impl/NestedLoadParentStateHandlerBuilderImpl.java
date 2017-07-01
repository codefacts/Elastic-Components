package elasta.composer.state.handlers.builder.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.composer.*;
import elasta.composer.state.handlers.builder.NestedLoadParentStateHandlerBuilder;
import elasta.composer.state.handlers.builder.ex.InvalidValueException;
import elasta.composer.state.handlers.builder.ex.MultipleResultException;
import elasta.composer.state.handlers.builder.ex.NoResultFoundException;
import elasta.composer.state.handlers.builder.ex.ParentDoesNotExistsException;
import elasta.core.flow.Flow;
import elasta.core.touple.immutable.Tpls;
import elasta.orm.BaseOrm;
import elasta.sql.JsonOps;
import elasta.orm.query.QueryExecutor;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 5/24/2017.
 */
final public class NestedLoadParentStateHandlerBuilderImpl implements NestedLoadParentStateHandlerBuilder {
    final String rootEntity;
    final String rootAlias = "r";
    final NestedResourcePathTranslator nestedResourcePathTranslator;
    final BaseOrm baseOrm;

    public NestedLoadParentStateHandlerBuilderImpl(String rootEntity, NestedResourcePathTranslator nestedResourcePathTranslator, BaseOrm baseOrm) {
        Objects.requireNonNull(rootEntity);
        Objects.requireNonNull(nestedResourcePathTranslator);
        Objects.requireNonNull(baseOrm);
        this.rootEntity = rootEntity;
        this.nestedResourcePathTranslator = nestedResourcePathTranslator;
        this.baseOrm = baseOrm;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonObject> build() {
        return msg -> {

            NestedResourcePathTranslator.QueryParamsAndFullPath paramsAndFullPath = nestedResourcePathTranslator.translate(
                rootEntity,
                rootAlias,
                msg.headers().get(ComposerCnsts.resourcePath).get()
            );

            return baseOrm
                .query(
                    toQueryParams(paramsAndFullPath)
                )
                .map(jsonObjects -> {

                    if (jsonObjects.size() <= 0) {
                        throw new ParentDoesNotExistsException("Parent does not exists for resource path '" + paramsAndFullPath.getFullPath() + "'");
                    }

                    if (jsonObjects.size() > 1) {
                        throw new MultipleResultException("Multiple results found but expected single result");
                    }

                    return Tpls.of(
                        jsonObjects.get(0),
                        paramsAndFullPath.getFullPath()
                    );
                })
                .map(tpl2 -> tpl2.apply((jsonObject, pathExpression) -> {
                    List<String> parts = pathExpression.parts();
                    for (int i = 1; i < parts.size(); i++) {

                        final Object value = jsonObject.getValue(parts.get(i));

                        jsonObject = toJsonObject(value)
                            .flatMap(vv -> toJsonArray(value))
                            .orElseThrow(() -> new InvalidValueException("Invalid value '" + value + "', expected JsonObject or JsonArray"));
                        ;
                    }

                    return jsonObject;
                }))
                .map(jsonObject -> Flow.trigger(Events.next, msg.withBody(jsonObject)))
                ;
        };
    }

    private QueryExecutor.QueryParams toQueryParams(NestedResourcePathTranslator.QueryParamsAndFullPath paramsAndFullPath) {
        return QueryExecutor.QueryParams.builder()
            .entity(rootEntity)
            .alias(rootAlias)
            .selections(
                paramsAndFullPath.getSelections()
            )
            .joinParams(paramsAndFullPath.getJoins())
            .criteria(
                toCriteria(paramsAndFullPath.getCriterias())
            )
            .having(ComposerUtils.emptyJsonObject())
            .orderBy(ImmutableSet.of())
            .groupBy(ImmutableSet.of())
            .build();
    }

    private JsonObject toCriteria(List<NestedResourcePathTranslator.PathAndValue> criterias) {

        ImmutableList.Builder<JsonObject> builder = ImmutableList.builder();

        criterias.forEach(pathAndValue -> {
            builder.add(
                JsonOps.eq(pathAndValue.getPathExpression().toString(), pathAndValue.getValue())
            );
        });

        return JsonOps.and(
            builder.build()
        );
    }

    private Optional<JsonObject> toJsonArray(Object value) {

        if (value instanceof JsonArray || value instanceof List) {

            JsonArray jsonArray = value instanceof JsonArray
                ? (JsonArray) value
                : new JsonArray(castToList(value));

            if (jsonArray.size() <= 0) {
                throw new NoResultFoundException();
            }

            if (jsonArray.size() > 1) {
                throw new MultipleResultException();
            }

            return Optional.of(
                jsonArray.getJsonObject(0)
            );

        }
        return Optional.empty();
    }

    private Optional<JsonObject> toJsonObject(Object value) {

        if (value instanceof JsonObject || value instanceof Map) {

            return Optional.of(
                value instanceof JsonObject
                    ? (JsonObject) value
                    : new JsonObject(castToMap(value))
            );

        }

        return Optional.empty();
    }

    private List castToList(Object value) {
        return (List) value;
    }

    private Map<String, Object> castToMap(Object value) {
        return ((Map<String, Object>) value);
    }
}
