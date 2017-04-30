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
class OptionalDataMapBuilder {
    final String rootEntity;
    final Set<FieldExpression> optionalFieldExpressions;
    final EntityMappingHelper helper;

    OptionalDataMapBuilder(String rootEntity, Set<FieldExpression> optionalFieldExpressions, EntityMappingHelper helper) {
        Objects.requireNonNull(rootEntity);
        Objects.requireNonNull(optionalFieldExpressions);
        Objects.requireNonNull(helper);
        this.rootEntity = rootEntity;
        this.optionalFieldExpressions = optionalFieldExpressions;
        this.helper = helper;
    }

    public Map<PathExpression, OptionalData> build() {
        return toOptionalDataMap();
    }

    private Map<PathExpression, OptionalData> toOptionalDataMap() {

        final Map<PathExpression, OptionalData> root = new HashMap<>();

        optionalFieldExpressions.forEach(fieldExpression -> {

            OptionalData optionalData = getAndSetDataRecursive(root, fieldExpression.getParent(), rootEntity);

            optionalData.getFields().add(fieldExpression.getField());
        });

        return root;
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
