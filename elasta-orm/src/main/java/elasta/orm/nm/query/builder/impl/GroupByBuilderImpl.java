package elasta.orm.nm.query.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.nm.query.FieldExpression;
import elasta.orm.nm.query.builder.GroupByBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class GroupByBuilderImpl implements GroupByBuilder {
    final List<FieldExpression> fieldExpressionListBuilder = new ArrayList<>();

    @Override
    public GroupByBuilder add(FieldExpression fieldExpression) {
        fieldExpressionListBuilder.add(fieldExpression);
        return this;
    }

    @Override
    public GroupByBuilder add(List<FieldExpression> fieldExpressions) {
        fieldExpressionListBuilder.addAll(fieldExpressions);
        return this;
    }

    @Override
    public List<FieldExpression> build() {
        return ImmutableList.copyOf(fieldExpressionListBuilder);
    }

    public List<FieldExpression> getFieldExpressionListBuilder() {
        return fieldExpressionListBuilder;
    }
}
