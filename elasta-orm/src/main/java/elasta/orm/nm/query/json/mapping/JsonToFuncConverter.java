package elasta.orm.nm.query.json.mapping;

import elasta.orm.nm.query.Func;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface JsonToFuncConverter {
    Func convert(JsonObject jsonObject, JsonToFuncConverterRegistry registry);
}
