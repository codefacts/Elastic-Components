package elasta.orm.query.expression.builder.impl;

import elasta.criteria.ParamsBuilder;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.builder.FieldExpressionHolderFunc;
import elasta.orm.query.expression.builder.FieldExpressionResolver;

import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class FieldExpressionHolderFuncImpl implements FieldExpressionHolderFunc {
    final FieldExpression fieldExpression;
    final FieldExpressionResolver fieldExpressionResolver;

    public FieldExpressionHolderFuncImpl(FieldExpression fieldExpression, FieldExpressionResolver fieldExpressionResolver) {
        Objects.requireNonNull(fieldExpression);
        Objects.requireNonNull(fieldExpressionResolver);
        this.fieldExpression = fieldExpression;
        this.fieldExpressionResolver = fieldExpressionResolver;
    }

    @Override
    public String get(ParamsBuilder paramsBuilder) {
        return fieldExpressionResolver.resolve(fieldExpression, paramsBuilder);
    }

    @Override
    public String toString() {
        return "FieldExpressionHolderFuncImpl{" +
            fieldExpression +
            '}';
    }
}
