package elasta.orm.impl;

import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.impl.QueryDataLoaderImpl.OptionalData;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;

import java.util.*;

/**
 * Created by sohan on 4/30/2017.
 */
final class OptionalDataMapBuilder {
    final String rootEntity;
    final String rootAlias;
    final Set<FieldExpression> optionalFieldExpressions;
    final Map<String, PathExpression> aliasToFullPathExpressionMap;
    final EntityMappingHelper helper;

    OptionalDataMapBuilder(String rootEntity, String rootAlias, Set<FieldExpression> optionalFieldExpressions, Map<String, PathExpression> aliasToFullPathExpressionMap, EntityMappingHelper helper) {
        Objects.requireNonNull(rootEntity);
        Objects.requireNonNull(rootAlias);
        Objects.requireNonNull(optionalFieldExpressions);
        Objects.requireNonNull(aliasToFullPathExpressionMap);
        Objects.requireNonNull(helper);
        this.rootEntity = rootEntity;
        this.rootAlias = rootAlias;
        this.optionalFieldExpressions = optionalFieldExpressions;
        this.aliasToFullPathExpressionMap = aliasToFullPathExpressionMap;
        this.helper = helper;
    }

    public Map<String, Map<PathExpression, OptionalData>> build() {
        return toOptionalDataMap();
    }

    private Map<String, Map<PathExpression, OptionalData>> toOptionalDataMap() {

        final Map<String, Map<PathExpression, OptionalData>> rootMap = new HashMap<>();

        optionalFieldExpressions.forEach(fieldExpression -> {

            final PathExpression pathExpression = fieldExpression.getParent();
            final String alias = pathExpression.root();

            Map<PathExpression, OptionalData> optionalDataMap = getRootOptionalDataMap(
                rootMap,
                alias
            );

            OptionalData optionalData = getAndSetDataRecursive(optionalDataMap, pathExpression, rootEntity(alias));

            optionalData.getFields().add(fieldExpression.getField());
        });

        return rootMap;
    }

    private String rootEntity(String alias) {
        if (Objects.equals(alias, rootAlias)) {
            return rootEntity;
        }
        return helper.getReferencingEntity(rootEntity, new FieldExpressionImpl(
            aliasToFullPathExpressionMap.get(alias)
        ));
    }

    private Map<PathExpression, OptionalData> getRootOptionalDataMap(final Map<String, Map<PathExpression, OptionalData>> root, final String rootAlias) {
        Map<PathExpression, OptionalData> optionalDataMap = root.get(rootAlias);
        if (optionalDataMap == null) {
            root.put(rootAlias, optionalDataMap = new LinkedHashMap<>());
        }

        return optionalDataMap;
    }

    private OptionalData getAndSetDataRecursive(Map<PathExpression, OptionalData> map, PathExpression pathExpression, String entity) {

        for (int index = 2, size = pathExpression.size(); index < size; index++) {

            final PathExpression subPath = pathExpression.subPath(0, index);

            OptionalData optionalData = map.get(subPath);

            if (optionalData == null) {
                continue;
            }

            return getAndSetDataRecursive(
                optionalData.getPathExpressionToOptionalDataMap(),
                PathExpression.create("r").concat(
                    pathExpression.subPath(
                        subPath.size(),
                        pathExpression.size()
                    )
                ),
                helper.getReferencingEntity(entity, new FieldExpressionImpl(subPath))
            );
        }

        OptionalData optionalData = map.get(pathExpression);

        if (optionalData == null) {
            map.put(
                pathExpression,
                optionalData = new OptionalData(
                    entity,
                    pathExpression,
                    new LinkedHashSet<>(),
                    new HashMap<>()
                )
            );
        }

        return optionalData;
    }
}
