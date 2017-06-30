package elasta.composer.state.handlers.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.GenerateResponseStateHandlerBuilder;
import elasta.composer.respose.generator.ResponseGenerator;
import elasta.composer.state.handlers.impl.GenerateResponseStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class GenerateResponseStateHandlerBuilderImpl implements GenerateResponseStateHandlerBuilder {
    final ResponseGenerator<Object, Object> responseGenerator;

    public GenerateResponseStateHandlerBuilderImpl(ResponseGenerator responseGenerator) {
        Objects.requireNonNull(responseGenerator);
        this.responseGenerator = responseGenerator;
    }

    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {
        return new GenerateResponseStateHandlerImpl(
            responseGenerator
        );
    }
}
