package elasta.composer.state.handlers.response.generator;

import elasta.core.intfs.Fun1Unckd;
import io.vertx.core.json.JsonObject;

import java.util.function.Function;

/**
 * Created by sohan on 5/12/2017.
 */
public interface ResponseGenerator<T, R> extends Fun1Unckd<T, R> {
}
