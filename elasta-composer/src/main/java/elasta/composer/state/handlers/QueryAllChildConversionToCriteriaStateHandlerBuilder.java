package elasta.composer.state.handlers;

import elasta.composer.MsgEnterEventHandlerP;

/**
 * Created by sohan on 5/29/2017.
 */
public interface QueryAllChildConversionToCriteriaStateHandlerBuilder extends StateHandlerBuilder {
    @Override
    MsgEnterEventHandlerP build();
}
