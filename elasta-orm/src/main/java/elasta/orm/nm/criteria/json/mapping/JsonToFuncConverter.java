package elasta.orm.nm.criteria.json.mapping;

import elasta.orm.nm.criteria.Func;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface JsonToFuncConverter {
    Func convert(JsonObject jsonObject, JsonToFuncConverterRegistry registry);
}
