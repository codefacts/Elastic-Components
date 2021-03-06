package elasta.composer.state.handlers.impl;

import elasta.composer.Msg;
import elasta.composer.state.handlers.EndStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

/**
 * Created by sohan on 6/30/2017.
 */
final public class EndStateHandlerImpl implements EndStateHandler<Object, Object> {
    @Override
    public Promise<StateTrigger<Msg<Object>>> handle(Msg<Object> msg) throws Throwable {
        return Promises.of(
            Flow.triggerExit(msg)
        );
    }
}
