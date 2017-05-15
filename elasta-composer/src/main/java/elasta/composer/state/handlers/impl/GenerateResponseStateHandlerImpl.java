package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
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
    final ResponseGenerator<Object, Object> responseGenerator;

    public GenerateResponseStateHandlerImpl(ResponseGenerator responseGenerator) {
        Objects.requireNonNull(responseGenerator);
        this.responseGenerator = responseGenerator;
    }

    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {
        return msg -> {

            Object response = responseGenerator.apply(msg.body());

            return Promises.of(
                Flow.trigger(Events.next, msg.withBody(response))
            );
        };
    }
}
