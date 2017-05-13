package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.Events;
import elasta.composer.state.handlers.ConversionToCriteriaStateHandlerBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.orm.impl.OperatorUtils;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class ConversionToCriteriaStateHandlerBuilderImpl implements ConversionToCriteriaStateHandlerBuilder {
    final String fieldExpression;

    public ConversionToCriteriaStateHandlerBuilderImpl(FieldExpression fieldExpression) {
        Objects.requireNonNull(fieldExpression);
        this.fieldExpression = fieldExpression.toString();
    }

    @Override
    public EnterEventHandlerP build() {
        return value -> {

            final JsonObject criteria = OperatorUtils.eq(fieldExpression, value);

            return Promises.of(
                Flow.trigger(Events.next, criteria)
            );
        };
    }
}
