package elasta.composer.respose.generator.impl;

import elasta.composer.respose.generator.JsonObjectResponseGenerator;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/30/2017.
 */
final public class JsonObjectResponseGeneratorImpl<T> implements JsonObjectResponseGenerator<T> {
    @Override
    public JsonObject apply(T t) throws Throwable {
        return ((JsonObject) t);
    }
}
