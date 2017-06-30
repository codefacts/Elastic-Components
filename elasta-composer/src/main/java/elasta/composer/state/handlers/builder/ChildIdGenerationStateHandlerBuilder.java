package elasta.composer.state.handlers.builder;

import elasta.composer.MsgEnterEventHandlerP;

/**
 * Created by sohan on 5/25/2017.
 */
public interface ChildIdGenerationStateHandlerBuilder extends StateHandlerBuilder {
    @Override
    MsgEnterEventHandlerP build();
}
