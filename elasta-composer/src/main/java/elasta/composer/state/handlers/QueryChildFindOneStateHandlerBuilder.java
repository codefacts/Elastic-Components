package elasta.composer.state.handlers;

import elasta.composer.MsgEnterEventHandlerP;

/**
 * Created by sohan on 5/28/2017.
 */
public interface QueryChildFindOneStateHandlerBuilder extends StateHandlerBuilder {
    @Override
    MsgEnterEventHandlerP build();
}
