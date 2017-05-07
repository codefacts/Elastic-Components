package elasta.orm.impl;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import elasta.commons.Utils;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.ex.QueryDataLoaderException;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;

import java.util.*;

/**
 * Created by sohan on 5/1/2017.
 */
final class MandatoryAndOptionalFieldsBuilder {
    final EntityMappingHelper helper;
    final Map<String, PathExpression> aliasToFullPathExpressionMap;

    MandatoryAndOptionalFieldsBuilder(EntityMappingHelper helper, Map<String, PathExpression> aliasToFullPathExpressionMap) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(aliasToFullPathExpressionMap);
        this.helper = helper;
        this.aliasToFullPathExpressionMap = aliasToFullPathExpressionMap;
    }

    QueryDataLoaderImpl.MandatoryAndOptionalFields build(QueryExecutor.QueryParams params) {

        ImmutableSet.Builder<FieldExpression> mandatoryListBuilder = ImmutableSet.builder();
        ImmutableSortedSet.Builder<FieldExpression> optionalListBuilder = ImmutableSortedSet.orderedBy(orderByFieldSize());
        ImmutableSetMultimap.Builder<String, String> aliasToFieldsMapBuilder = ImmutableSetMultimap.builder();

        final Set<PathExpression> optionalPaths = new OptionalListBuilder(helper, params, aliasToFullPathExpressionMap).build();

        Map<String, Map<String, Map<String, Object>>> optionalPathsMaps = toOptionalPathsMaps(optionalPaths);

        params.getSelections().forEach(fieldExpression -> {

            final PathExpression pathExpression = fieldExpression.getParent();
            final String alias = pathExpression.root();

            {
                boolean aliasNotFound = Utils.not(
                    aliasToFullPathExpressionMap.containsKey(alias) || Objects.equals(alias, params.getAlias())
                );

                if (aliasNotFound) {
                    throw new QueryDataLoaderException("Root alias '" + alias + "' is not found in valid alias list " + aliasToFullPathExpressionMap.keySet());
                }
            }

            if (helper.isMandatory(params.getEntity(), toFullPathExp(pathExpression, params.getAlias(), aliasToFullPathExpressionMap))) {

                mandatoryListBuilder.add(fieldExpression);

            } else if (startsWithInOptionalPaths(optionalPathsMaps, pathExpression)) {

                mandatoryListBuilder.add(fieldExpression);

            } else if (fieldExpression.size() > 2) {

                optionalListBuilder.add(fieldExpression);
            } else {

                aliasToFieldsMapBuilder.put(fieldExpression.getParent().root(), fieldExpression.getField());
            }
        });

        return new QueryDataLoaderImpl.MandatoryAndOptionalFields(
            mandatoryListBuilder.build(),
            optionalListBuilder.build(),
            aliasToFieldsMapBuilder.build()
        );
    }

    private PathExpression toFullPathExp(PathExpression pathExpression, String rootAlias, Map<String, PathExpression> aliasToFullPathExpressionMap) {
        final String alias = pathExpression.root();
        if (Objects.equals(alias, rootAlias)) {
            return pathExpression;
        }
        return aliasToFullPathExpressionMap.get(alias).concat(
            pathExpression.subPath(1, pathExpression.size())
        );
    }

    private Map<String, String> aliasToEntityMap(final String rootAlias, final String rootEntity, final Collection<QueryExecutor.JoinParam> joinParams) {

        final Map<String, PathExpression> aliasToExpMap = toAliasToExpMap(joinParams);

        final ImmutableMap.Builder<String, String> aliasToEntityMapBuilder = ImmutableMap.builder();

        aliasToEntityMapBuilder.put(rootAlias, rootEntity);

        joinParams.forEach(
            joinParam -> aliasToEntityMapBuilder.put(
                joinParam.getAlias(),
                helper.getReferencingEntity(
                    getEntity(rootAlias, rootEntity, aliasToExpMap, joinParam.getPath().root()),
                    new FieldExpressionImpl(joinParam.getPath())
                )
            )
        );

        return aliasToEntityMapBuilder.build();
    }

    private String getEntity(final String rootAlias, final String rootEntity, final Map<String, PathExpression> aliasToExpMap, final String alias) {

        if (Objects.equals(alias, rootAlias)) {
            return rootEntity;
        }

        final PathExpression pathExpression = aliasToExpMap.get(alias);

        Objects.requireNonNull(pathExpression);

        return helper.getReferencingEntity(
            getEntity(rootAlias, rootEntity, aliasToExpMap, pathExpression.root()),
            new FieldExpressionImpl(pathExpression)
        );
    }

    private Map<String, PathExpression> toAliasToExpMap(Collection<QueryExecutor.JoinParam> joinParams) {
        ImmutableMap.Builder<String, PathExpression> mapBuilder = ImmutableMap.builder();
        joinParams.forEach(joinParam -> {
            mapBuilder.put(joinParam.getPath().root(), joinParam.getPath());
        });

        return mapBuilder.build();
    }

    private Comparator<FieldExpression> orderByFieldSize() {
        return (o1, o2) -> {

            if (o1.equals(o2)) {
                return 0;
            }

            final int diff = o1.size() - o2.size();

            return diff != 0 ? diff : o1.toString().compareTo(o2.toString());
        };
    }

    private boolean startsWithInOptionalPaths(final Map<String, Map<String, Map<String, Object>>> map, PathExpression pathExpression) {

        Map<String, Map<String, Map<String, Object>>> m = map;

        for (String part : pathExpression.parts()) {
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
}
