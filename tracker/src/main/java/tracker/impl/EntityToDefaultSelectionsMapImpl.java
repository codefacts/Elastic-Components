package tracker.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import tracker.EntityToDefaultSelectionsMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/30/2017.
 */
final public class EntityToDefaultSelectionsMapImpl implements EntityToDefaultSelectionsMap {
    final String rootAlias;
    final EntityMappingHelper helper;
    final Map<EntityAndAction, SelectionsAndJoinParams> entityToFieldExpressionsMap;

    public EntityToDefaultSelectionsMapImpl(String rootAlias, EntityMappingHelper helper, Map<EntityAndAction, SelectionsAndJoinParams> entityToFieldExpressionsMap) {
        Objects.requireNonNull(rootAlias);
        Objects.requireNonNull(helper);
        Objects.requireNonNull(entityToFieldExpressionsMap);
        this.rootAlias = rootAlias;
        this.helper = helper;
        this.entityToFieldExpressionsMap = entityToFieldExpressionsMap;
    }

    @Override
    public SelectionsAndJoinParams fieldSelections(EntityAndAction entityAndAction) {
        SelectionsAndJoinParams fieldExpressions = entityToFieldExpressionsMap.get(entityAndAction);
        if (fieldExpressions != null) {
            return fieldExpressions;
        }
        return defaultSelections(entityAndAction.getEntity());
    }

    private SelectionsAndJoinParams defaultSelections(String entity) {
        return new SelectionsAndJoinParams(
            Arrays.stream(helper.getFields(entity))
                .filter(field -> !field.getRelationship().isPresent())
                .map(field -> new FieldExpressionImpl(rootAlias + "." + field.getName()))
                .collect(Collectors.toList()),
            ImmutableList.of()
        );
    }
}
