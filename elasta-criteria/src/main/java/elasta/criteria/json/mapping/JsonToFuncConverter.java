package elasta.criteria.json.mapping;

import elasta.criteria.Func;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface JsonToFuncConverter {
    Func convert(JsonObject jsonObject, JsonToFuncConverterMap converterMap);
}
