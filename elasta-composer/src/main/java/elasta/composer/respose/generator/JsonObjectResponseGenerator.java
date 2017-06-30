package elasta.composer.respose.generator;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/12/2017.
 */
public interface JsonObjectResponseGenerator<T> extends ResponseGenerator<T, JsonObject> {
    @Override
    JsonObject apply(T t) throws Throwable;
}
