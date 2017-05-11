package elasta.eventbus;

import io.vertx.core.eventbus.Message;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 5/9/2017.
 */
@FunctionalInterface
public interface EventBusErrorHandler<T> {

    void handleError(HandleErrorParams<T> params);

    @Value
    @Builder
    final class HandleErrorParams<T> {
        final Throwable throwable;
        final String address;
        final Message<T> message;
        final Object errorValue;

        public HandleErrorParams(Throwable throwable, String address, Message<T> message, Object errorValue) {
            Objects.requireNonNull(throwable);
            Objects.requireNonNull(address);
            Objects.requireNonNull(message);
            this.throwable = throwable;
            this.address = address;
            this.message = message;
            this.errorValue = errorValue == null ? null : errorValue;
        }

        public Optional<Object> getErrorValue() {
            return Optional.ofNullable(errorValue);
        }
    }
}
