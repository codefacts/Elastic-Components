package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.state.handlers.StartStateHandlerBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;

/**
 * Created by sohan on 5/12/2017.
 */
final public class StartStateHandlerBuilderImpl implements StartStateHandlerBuilder {
    @Override
    public EnterEventHandlerP build() {
        return request -> Promises.of(
            Flow.trigger(Events.next, request)
        );
    }
}
