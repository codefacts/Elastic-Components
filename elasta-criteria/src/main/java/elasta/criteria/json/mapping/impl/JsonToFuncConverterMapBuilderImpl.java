package elasta.criteria.json.mapping.impl;

import com.google.common.collect.ImmutableMap;
import elasta.criteria.Func;
import elasta.criteria.funcs.ops.*;
import elasta.criteria.json.mapping.*;
import io.vertx.core.json.JsonArray;

import java.util.Objects;

/**
 * Created by sohan on 3/20/2017.
 */
final public class JsonToFuncConverterMapBuilderImpl implements JsonToFuncConverterMapBuilder {
    final JsonToFuncConverterHelper jsonToFuncConverterHelper;

    public JsonToFuncConverterMapBuilderImpl(JsonToFuncConverterHelper jsonToFuncConverterHelper) {
        Objects.requireNonNull(jsonToFuncConverterHelper);
        this.jsonToFuncConverterHelper = jsonToFuncConverterHelper;
    }

    @Override
    public JsonToFuncConverterMap build() {

        ImmutableMap.Builder<String, JsonToFuncConverter> builder = ImmutableMap.builder();

        builder.put(OpNames.and, jsonToFuncConverterHelper.arrayOp(Ops::and));
        builder.put(OpNames.or, jsonToFuncConverterHelper.arrayOp(Ops::or));

        builder.put(OpNames.not, jsonToFuncConverterHelper.op1(Ops::not));

        builder.put(OpNames.eq, jsonToFuncConverterHelper.op2(Ops::eq));
        builder.put(OpNames.ne, jsonToFuncConverterHelper.op2(Ops::ne));
        builder.put(OpNames.lt, jsonToFuncConverterHelper.op2(Ops::lt));
        builder.put(OpNames.lte, jsonToFuncConverterHelper.op2(Ops::lte));
        builder.put(OpNames.gt, jsonToFuncConverterHelper.op2(Ops::gt));
        builder.put(OpNames.gte, jsonToFuncConverterHelper.op2(Ops::gte));

        builder.put(OpNames.count, jsonToFuncConverterHelper.op1(Ops::count));
        builder.put(OpNames.distinct, jsonToFuncConverterHelper.op1(Ops::distinct));

        builder.put(OpNames.in, (jsonObject, converterMap) -> Ops.in(
            jsonToFuncConverterHelper.toFunc(
                jsonObject.getValue("arg1"),
                converterMap
            ),
            toFuncArray(
                jsonObject.getJsonArray("args"),
                converterMap
            )
        ));

        return new JsonToFuncConverterMapImpl(
            builder.build()
        );
    }

    private Func[] toFuncArray(JsonArray args, JsonToFuncConverterMap converterMap) {
        Func[] funcs = new Func[args.size()];
        for (int i = 0; i < args.size(); i++) {
            funcs[i] = jsonToFuncConverterHelper.toFunc(
                args.getValue(i),
                converterMap
            );
        }
        return funcs;
    }
}
