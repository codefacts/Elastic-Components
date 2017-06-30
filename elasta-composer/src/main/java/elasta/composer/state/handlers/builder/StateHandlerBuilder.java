package elasta.composer.state.handlers.builder;

import elasta.composer.MsgEnterEventHandlerP;
import elasta.core.flow.EnterEventHandler;
import elasta.core.flow.EnterEventHandlerP;

/**
 * Created by sohan on 5/12/2017.
 */
@FunctionalInterface
public interface StateHandlerBuilder<T, R> {
    MsgEnterEventHandlerP<T, R> build();
}
