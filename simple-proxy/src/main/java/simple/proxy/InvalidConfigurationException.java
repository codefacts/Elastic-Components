package simple.proxy;

import java.io.IOException;

/**
 * Created by sohan on 2017-08-05.
 */
final public class InvalidConfigurationException extends RuntimeException {
    public InvalidConfigurationException(IOException e) {
        super(e);
    }
}
