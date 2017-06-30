package elasta.composer.respose.generator.impl;

import elasta.composer.respose.generator.JsonArrayResponseGenerator;
import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 6/30/2017.
 */
final public class JsonArrayResponseGeneratorImpl<T> implements JsonArrayResponseGenerator<T> {
    @Override
    public JsonArray apply(T t) throws Throwable {
        return ((JsonArray) t);
    }
}
