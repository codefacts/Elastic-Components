package elasta.composer.state.handlers;

import elasta.composer.MsgEnterEventHandlerP;

/**
 * Created by sohan on 5/24/2017.
 */
public interface NestedLoadParentStateHandlerBuilder extends StateHandlerBuilder {
    @Override
    MsgEnterEventHandlerP build();
}
