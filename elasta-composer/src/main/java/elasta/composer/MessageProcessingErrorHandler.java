package elasta.composer;

import io.vertx.core.eventbus.Message;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 6/29/2017.
 */
public interface MessageProcessingErrorHandler {

    void handleError(Throwable throwable, Message message);
}
