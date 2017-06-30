package elasta.composer.state.handlers.builder.impl;

import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.EndStateHandlerBuilder;
import elasta.composer.state.handlers.impl.EndStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;

/**
 * Created by sohan on 5/12/2017.
 */
final public class EndStateHandlerBuilderImpl implements EndStateHandlerBuilder {
    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {
        return new EndStateHandlerImpl();
    }
}
