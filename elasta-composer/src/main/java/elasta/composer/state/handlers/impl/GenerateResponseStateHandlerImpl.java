package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.respose.generator.ResponseGenerator;
import elasta.composer.state.handlers.GenerateResponseStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class GenerateResponseStateHandlerImpl implements GenerateResponseStateHandler<Object, Object> {
    final ResponseGenerator<Object, Object> responseGenerator;

    public GenerateResponseStateHandlerImpl(ResponseGenerator responseGenerator) {
        Objects.requireNonNull(responseGenerator);
        this.responseGenerator = responseGenerator;
    }

    @Override
    public Promise<StateTrigger<Msg<Object>>> handle(Msg<Object> msg) throws Throwable {

        Object response = responseGenerator.apply(msg.body());

        return Promises.of(
            Flow.trigger(Events.next, msg.withBody(response))
        );
    }
}
