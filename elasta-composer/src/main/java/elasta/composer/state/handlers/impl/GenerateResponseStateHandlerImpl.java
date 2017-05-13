package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.state.handlers.GenerateResponseStateHandler;
import elasta.composer.state.handlers.response.generator.ResponseGenerator;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class GenerateResponseStateHandlerImpl implements GenerateResponseStateHandler {
    final ResponseGenerator responseGenerator;

    public GenerateResponseStateHandlerImpl(ResponseGenerator responseGenerator) {
        Objects.requireNonNull(responseGenerator);
        this.responseGenerator = responseGenerator;
    }

    @Override
    public EnterEventHandlerP build() {
        return request -> {

            Object response = responseGenerator.apply(request);

            return Promises.of(
                Flow.trigger(Events.next, response)
            );
        };
    }
}
