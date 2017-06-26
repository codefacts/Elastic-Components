package elasta.criteria.json.mapping;

import elasta.criteria.Func;
import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 3/20/2017.
 */
public interface JsonToFuncConverterBuilderHelper {

    Func toFunc(Object value, JsonToFuncConverterMap converterMap);
}
