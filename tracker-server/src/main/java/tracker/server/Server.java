package tracker.server;

import lombok.Builder;
import lombok.Value;

/**
 * Created by sohan on 7/1/2017.
 */
public interface Server {

    Server start(Config config);

    Server stop();

    @Value
    @Builder
    final class Config {

    }
}
