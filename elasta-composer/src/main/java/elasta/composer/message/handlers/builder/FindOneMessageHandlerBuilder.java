package elasta.composer.message.handlers.builder;

import elasta.composer.message.handlers.MessageHandler;
import elasta.core.flow.Flow;

/**
 * Created by sohan on 5/14/2017.
 */
public interface FindOneMessageHandlerBuilder {
    MessageHandler<Object> build();
}
