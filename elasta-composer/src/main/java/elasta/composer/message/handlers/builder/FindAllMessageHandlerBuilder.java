package elasta.composer.message.handlers.builder;

import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.JsonObjectMessageHandlerBuilder;

/**
 * Created by sohan on 5/14/2017.
 */
public interface FindAllMessageHandlerBuilder extends JsonObjectMessageHandlerBuilder {
    @Override
    JsonObjectMessageHandler build();
}
