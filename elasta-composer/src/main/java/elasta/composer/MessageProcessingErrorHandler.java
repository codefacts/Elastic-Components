package elasta.composer;

import io.vertx.core.eventbus.Message;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 6/29/2017.
 */
public interface MessageProcessingErrorHandler {

    void onError(Params params);

    @Value
    @Builder
    final class Params {
        final Throwable ex;
        final Message message;

        public Params(Throwable ex, Message message) {
            Objects.requireNonNull(ex);
            Objects.requireNonNull(message);
            this.ex = ex;
            this.message = message;
        }

        public <T> Message<T> getMessage() {
            return message;
        }
    }
}
