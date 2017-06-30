package elasta.composer.state.handlers.builder;

import elasta.composer.MsgEnterEventHandlerP;

/**
 * Created by sohan on 5/12/2017.
 */
public interface ConversionToCriteriaStateHandlerBuilder extends StateHandlerBuilder {
    @Override
    MsgEnterEventHandlerP build();
}
