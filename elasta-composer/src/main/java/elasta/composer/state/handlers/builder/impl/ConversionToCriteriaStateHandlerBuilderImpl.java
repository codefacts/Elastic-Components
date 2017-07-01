package elasta.composer.state.handlers.builder.impl;

import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.ConversionToCriteriaStateHandlerBuilder;
import elasta.composer.state.handlers.impl.ConversionToCriteriaStateHandlerImpl;
import io.vertx.core.json.JsonObject;

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
