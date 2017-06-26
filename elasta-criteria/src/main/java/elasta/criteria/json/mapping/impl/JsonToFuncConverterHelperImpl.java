package elasta.criteria.json.mapping.impl;

import elasta.criteria.Func;
import elasta.criteria.json.mapping.*;
import io.vertx.core.json.JsonArray;

import java.util.Objects;

/**
 * Created by sohan on 6/27/2017.
 */
final public class JsonToFuncConverterHelperImpl implements JsonToFuncConverterHelper {
    final JsonToFuncConverterBuilderHelper jsonToFuncConverterBuilderHelper;

    public JsonToFuncConverterHelperImpl(JsonToFuncConverterBuilderHelper jsonToFuncConverterBuilderHelper) {
        Objects.requireNonNull(jsonToFuncConverterBuilderHelper);
        this.jsonToFuncConverterBuilderHelper = jsonToFuncConverterBuilderHelper;
    }

    public JsonToFuncConverter op1(Operation1Builder builder) {
        return (jsonObject, converterMap) -> builder.build(
            jsonToFuncConverterBuilderHelper.toFunc(
                jsonObject.getValue(Mp.arg),
                converterMap
            )
        );
    }

    public JsonToFuncConverter op2(Operation2Builder builder) {

        return (jsonObject, converterMap) -> {
            Func func1 = jsonToFuncConverterBuilderHelper.toFunc(
                jsonObject.getValue(Mp.arg1),
                converterMap
            );

            Func func2 = jsonToFuncConverterBuilderHelper.toFunc(
                jsonObject.getValue(Mp.arg2),
                converterMap
            );

            return builder.build(func1, func2);
        };
    }

    public JsonToFuncConverter op3(Operation3Builder builder) {

        return (jsonObject, converterMap) -> {
            Func func1 = jsonToFuncConverterBuilderHelper.toFunc(
                jsonObject.getValue(Mp.arg1),
                converterMap
            );

            Func func2 = jsonToFuncConverterBuilderHelper.toFunc(
                jsonObject.getValue(Mp.arg2),
                converterMap
            );

            Func func3 = jsonToFuncConverterBuilderHelper.toFunc(
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

                funcs[i] = jsonToFuncConverterBuilderHelper.toFunc(
                    args.getValue(i),
                    converterMap
                );
            }

            return opsBuilder.build(funcs);
        };
    }
}
