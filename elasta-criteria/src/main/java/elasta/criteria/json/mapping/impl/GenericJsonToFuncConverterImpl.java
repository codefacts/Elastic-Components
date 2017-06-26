package elasta.criteria.json.mapping.impl;

import elasta.criteria.Func;
import elasta.criteria.json.mapping.GenericJsonToFuncConverter;
import elasta.criteria.json.mapping.JsonToFuncConverter;
import elasta.criteria.json.mapping.JsonToFuncConverterMap;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/27/2017.
 */
final public class GenericJsonToFuncConverterImpl implements GenericJsonToFuncConverter {

    @Override
    public Func convert(JsonObject query, JsonToFuncConverterMap converterMap) {

        String op = query.getString("op");

        if (op == null) {
            return EmptyFunc();
        }

        JsonToFuncConverter converter = converterMap.get(op);

        return converter.convert(query, converterMap);
    }

    private Func EmptyFunc() {
        return paramsBuilder -> "";
    }
}
