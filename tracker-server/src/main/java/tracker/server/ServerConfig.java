package tracker.server;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 7/22/2017.
 */
@Value
@Builder
final public class ServerConfig {
    final String uploadDir;
    final String publicDir;

    public ServerConfig(String uploadDir, String publicDir) {
        Objects.requireNonNull(uploadDir);
        Objects.requireNonNull(publicDir);
        this.uploadDir = uploadDir;
        this.publicDir = publicDir;
    }
}
