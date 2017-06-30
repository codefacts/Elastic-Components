package elasta.composer.respose.generator;

import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 5/12/2017.
 */
public interface JsonArrayResponseGenerator<T> extends ResponseGenerator<T, JsonArray> {
    @Override
    JsonArray apply(T t) throws Throwable;
}
