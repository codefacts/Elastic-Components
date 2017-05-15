package elasta.composer.state.handlers;

import elasta.composer.MsgEnterEventHandlerP;
import elasta.core.flow.EnterEventHandlerP;

/**
 * Created by sohan on 5/12/2017.
 */
public interface AuthorizationStateHandlerBuilder extends StateHandlerBuilder {
    @Override
    MsgEnterEventHandlerP build();
}
