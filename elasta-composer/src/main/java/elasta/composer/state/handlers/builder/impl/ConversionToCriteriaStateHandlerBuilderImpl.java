package elasta.composer.state.handlers.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.ConversionToCriteriaStateHandlerBuilder;
import elasta.composer.state.handlers.builder.ex.ConversionToCriteriaStateHandlerException;
import elasta.composer.state.handlers.impl.ConversionToCriteriaStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.sql.SqlOps;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class ConversionToCriteriaStateHandlerBuilderImpl implements ConversionToCriteriaStateHandlerBuilder {
    final String alias;

    public ConversionToCriteriaStateHandlerBuilderImpl(String alias) {
        Objects.requireNonNull(alias);
        this.alias = alias;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return new ConversionToCriteriaStateHandlerImpl(
            alias
        );
    }
}
