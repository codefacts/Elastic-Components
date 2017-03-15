package elasta.criteria.json.mapping.impl;

import com.google.common.collect.ImmutableMap;
import elasta.criteria.Func;import elasta.criteria.funcs.ops.ComparisionOps;import elasta.criteria.funcs.ops.LogicalOps;import elasta.criteria.funcs.ops.ValueHolderOps;import elasta.criteria.json.mapping.*;import elasta.criteria.Func;
import elasta.criteria.funcs.ops.ComparisionOps;
import elasta.criteria.funcs.ops.LogicalOps;
import elasta.criteria.funcs.ops.ValueHolderOps;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Created by Jango on 2017-01-07.
 */
public class JsonToFuncConverterRegistryHelper {
    final Map<String, JsonToFuncConverter> converterMap;
    final LogicalOps logicalOps;
    final ComparisionOps comparisionOps;
    final ValueHolderOps valueHolderOps;
    final ValueHolderOperationBuilder valueHolderOperationBuilder;

    public JsonToFuncConverterRegistryHelper(LogicalOps logicalOps, ComparisionOps comparisionOps, ValueHolderOps valueHolderOps, ValueHolderOperationBuilder valueHolderOperationBuilder) {
        this.logicalOps = logicalOps;
        this.comparisionOps = comparisionOps;
        this.valueHolderOps = valueHolderOps;
        this.valueHolderOperationBuilder = valueHolderOperationBuilder;

        ImmutableMap.Builder<String, JsonToFuncConverter> builder = ImmutableMap.builder();

        builder.put(OpNames.and, arrayOp(logicalOps::and));
        builder.put(OpNames.or, arrayOp(logicalOps::or));
        builder.put(OpNames.not, op1(logicalOps::not));

        builder.put(OpNames.eq, op2(comparisionOps::eq));
        builder.put(OpNames.ne, op2(comparisionOps::ne));
        builder.put(OpNames.lt, op2(comparisionOps::lt));
        builder.put(OpNames.lte, op2(comparisionOps::lte));
        builder.put(OpNames.gt, op2(comparisionOps::gt));
        builder.put(OpNames.gte, op2(comparisionOps::gte));

        converterMap = builder.build();
    }

    private JsonToFuncConverter op1(Operation1Builder builder) {
        return (jsonObject, registry) -> {

            return builder.build(
                toFunc(
                    jsonObject.getValue(MpCnst.arg),
                    registry
                )
            );
        };
    }

    private JsonToFuncConverter op2(Operation2Builder builder) {

        return (jsonObject, registry) -> {
            Func func1 = toFunc(
                jsonObject.getValue(MpCnst.arg1),
                registry
            );

            Func func2 = toFunc(
                jsonObject.getValue(MpCnst.arg2),
                registry
            );

            return builder.build(func1, func2);
        };
    }

    private JsonToFuncConverter arrayOp(ArrayOperationBuilder opsBuilder) {
        return (jsonObject, registry) -> {

            JsonArray args = jsonObject.getJsonArray(MpCnst.args, MappingUtils.emptyJsonArray());

            Func[] funcs = new Func[args.size()];

            for (int i = 0; i < args.size(); i++) {

                funcs[i] = toFunc(
                    args.getValue(i),
                    registry
                );
            }

            return opsBuilder.build(funcs);
        };
    }

    private Func toFunc(Object value, JsonToFuncConverterRegistry registry) {

        if (value == null) {
            return valueHolderOperationBuilder.build(value);
        }

        if (value instanceof JsonObject) {
            JsonObject object = (JsonObject) value;

            String op = object.getString(MpCnst.op);
            return registry.get(op).convert(object, registry);
        }

        if (value instanceof Map) {

            JsonObject object = new JsonObject((Map<String, Object>) value);

            String op = object.getString(MpCnst.op);
            return registry.get(op).convert(object, registry);
        }

        return valueHolderOperationBuilder.build(value);
    }

    public Map<String, JsonToFuncConverter> getConverterMap() {
        return converterMap;
    }
}
