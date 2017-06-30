package elasta.composer.state.handlers.builder;

import elasta.composer.MsgEnterEventHandlerP;

/**
 * Created by sohan on 5/20/2017.
 */
public interface AuthorizeAllStateHandlerBuilder extends StateHandlerBuilder {
    @Override
    MsgEnterEventHandlerP build();
}
