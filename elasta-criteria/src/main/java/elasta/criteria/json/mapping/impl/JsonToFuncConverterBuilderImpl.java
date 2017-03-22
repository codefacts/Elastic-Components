package elasta.criteria.json.mapping.impl;

import com.google.common.collect.ImmutableMap;
import elasta.criteria.Func;
import elasta.criteria.funcs.ops.ComparisionOps;
import elasta.criteria.funcs.ops.LogicalOps;
import elasta.criteria.json.mapping.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
final public class JsonToFuncConverterBuilderImpl implements JsonToFuncConverterBuilder {
    final ValueHolderOperationBuilder valueHolderOperationBuilder;

    public JsonToFuncConverterBuilderImpl(ValueHolderOperationBuilder valueHolderOperationBuilder) {
        Objects.requireNonNull(valueHolderOperationBuilder);
        this.valueHolderOperationBuilder = valueHolderOperationBuilder;
    }

    public JsonToFuncConverter op1(Operation1Builder builder) {
        return (jsonObject, converterMap) -> builder.build(
            toFunc(
                jsonObject.getValue(Mp.arg),
                converterMap
            )
        );
    }

    public JsonToFuncConverter op2(Operation2Builder builder) {

        return (jsonObject, converterMap) -> {
            Func func1 = toFunc(
                jsonObject.getValue(Mp.arg1),
                converterMap
            );

            Func func2 = toFunc(
                jsonObject.getValue(Mp.arg2),
                converterMap
            );

            return builder.build(func1, func2);
        };
    }

    public JsonToFuncConverter op3(Operation3Builder builder) {

        return (jsonObject, converterMap) -> {
            Func func1 = toFunc(
                jsonObject.getValue(Mp.arg1),
                converterMap
            );

            Func func2 = toFunc(
                jsonObject.getValue(Mp.arg2),
                converterMap
            );

            Func func3 = toFunc(
                jsonObject.getValue(Mp.arg3),
                converterMap
            );

            return builder.build(func1, func2, func3);
        };
    }

    public JsonToFuncConverter arrayOp(ArrayOperationBuilder opsBuilder) {
        return (jsonObject, converterMap) -> {

            JsonArray args = jsonObject.getJsonArray(Mp.args, MappingUtils.emptyJsonArray());

            Func[] funcs = new Func[args.size()];

            for (int i = 0; i < args.size(); i++) {

                funcs[i] = toFunc(
                    args.getValue(i),
                    converterMap
                );
            }

            return opsBuilder.build(funcs);
        };
    }

    private Func toFunc(Object value, JsonToFuncConverterMap converterMap) {

        if (value == null) {
            return valueHolderOperationBuilder.build(value);
        }

        if (value instanceof JsonObject) {
            JsonObject object = (JsonObject) value;

            String op = object.getString(Mp.op);
            return converterMap.get(op).convert(object, converterMap);
        }

        if (value instanceof Map) {

            JsonObject object = new JsonObject((Map<String, Object>) value);

            String op = object.getString(Mp.op);
            return converterMap.get(op).convert(object, converterMap);
        }

        return valueHolderOperationBuilder.build(value);
    }
}
