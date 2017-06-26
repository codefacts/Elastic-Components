package elasta.criteria.json.mapping.impl;

import elasta.criteria.Func;
import elasta.criteria.json.mapping.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
final public class JsonToFuncConverterBuilderHelperImpl implements JsonToFuncConverterBuilderHelper {
    final ValueHolderOperationBuilderHelper valueHolderOperationBuilderHelper;

    public JsonToFuncConverterBuilderHelperImpl(ValueHolderOperationBuilderHelper valueHolderOperationBuilderHelper) {
        Objects.requireNonNull(valueHolderOperationBuilderHelper);
        this.valueHolderOperationBuilderHelper = valueHolderOperationBuilderHelper;
    }

    @Override
    public Func toFunc(Object value, JsonToFuncConverterMap converterMap) {

        if (value == null) {
            return valueHolderOperationBuilderHelper.build(value);
        }

        if (value instanceof JsonObject) {

            return convert((JsonObject) value, converterMap);
        }

        if (value instanceof Map) {

            return convert(new JsonObject((Map<String, Object>) value), converterMap);
        }

        return valueHolderOperationBuilderHelper.build(value);
    }

    private Func convert(JsonObject jsonObject, JsonToFuncConverterMap converterMap) {
        String op = jsonObject.getString(Mp.op);
        return converterMap.get(op).convert(jsonObject, converterMap);
    }
}
