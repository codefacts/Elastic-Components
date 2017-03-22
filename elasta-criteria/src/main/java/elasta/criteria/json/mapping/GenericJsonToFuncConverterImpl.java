package elasta.criteria.json.mapping;

import elasta.criteria.Func;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 3/21/2017.
 */
final public class GenericJsonToFuncConverterImpl implements JsonToFuncConverter {
    @Override
    public Func convert(JsonObject query, JsonToFuncConverterMap converterMap) {
        
        String op = query.getString("op");

        JsonToFuncConverter converter = converterMap.get(op);

        return converter.convert(query, converterMap);
    }
}
