package elasta.composer.state.handlers.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.StartStateHandlerBuilder;
import elasta.composer.state.handlers.impl.StartStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;

/**
 * Created by sohan on 5/12/2017.
 */
final public class StartStateHandlerBuilderImpl implements StartStateHandlerBuilder {
    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {
        return new StartStateHandlerImpl();
    }
}
