package tracker;

import elasta.composer.message.handlers.MessageHandler;
import elasta.module.ModuleSystem;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
public interface MessageHandlersBuilder {

    List<AddressAndHandler> build(ModuleSystem module);

    @Value
    @Builder
    final class AddressAndHandler {
        final String address;
        final MessageHandler messageHandler;

        public AddressAndHandler(String address, MessageHandler messageHandler) {
            Objects.requireNonNull(address);
            Objects.requireNonNull(messageHandler);
            this.address = address;
            this.messageHandler = messageHandler;
        }
    }
}
