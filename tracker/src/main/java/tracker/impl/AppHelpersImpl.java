package tracker.impl;

import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import tracker.AppHelpers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/30/2017.
 */
final public class AppHelpersImpl implements AppHelpers {
    final String rootAlias;
    final EntityMappingHelper helper;

    public AppHelpersImpl(String rootAlias, EntityMappingHelper helper) {
        Objects.requireNonNull(rootAlias);
        Objects.requireNonNull(helper);
        this.rootAlias = rootAlias;
        this.helper = helper;
    }

    @Override
    public List<FieldExpression> findOneFields(String entity) {
        return Arrays.stream(helper.getFields(entity))
            .filter(field -> !field.getRelationship().isPresent())
            .map(field -> new FieldExpressionImpl(rootAlias + "." + field.getName()))
            .collect(Collectors.toList());
    }

    @Override
    public List<FieldExpression> findAllFields(String entity) {
        return findOneFields(entity);
    }
}
