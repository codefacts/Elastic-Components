package elasta.composer.state.handlers.impl;

import elasta.composer.state.handlers.EndStateHandlerBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;

/**
 * Created by sohan on 5/12/2017.
 */
final public class EndStateHandlerBuilderImpl implements EndStateHandlerBuilder {
    @Override
    public EnterEventHandlerP build() {
        return message -> Promises.of(
            Flow.triggerExit(message)
        );
    }
}
