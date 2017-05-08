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
    final LogicalOps logicalOps;
    final ComparisionOps comparisionOps;
    final ValueHolderOps valueHolderOps;
    final ArrayOps arrayOps;
    final StringOps stringOps;
    final FunctionalOps functionalOps;
    final JsonToFuncConverterBuilder jsonToFuncConverterBuilder;

    public JsonToFuncConverterMapBuilderImpl(LogicalOps logicalOps, ComparisionOps comparisionOps, ValueHolderOps valueHolderOps, ArrayOps arrayOps, StringOps stringOps, FunctionalOps functionalOps, JsonToFuncConverterBuilder jsonToFuncConverterBuilder) {
        Objects.requireNonNull(logicalOps);
        Objects.requireNonNull(comparisionOps);
        Objects.requireNonNull(valueHolderOps);
        Objects.requireNonNull(jsonToFuncConverterBuilder);
        Objects.requireNonNull(arrayOps);
        Objects.requireNonNull(stringOps);
        Objects.requireNonNull(functionalOps);
        this.logicalOps = logicalOps;
        this.comparisionOps = comparisionOps;
        this.valueHolderOps = valueHolderOps;
        this.arrayOps = arrayOps;
        this.stringOps = stringOps;
        this.functionalOps = functionalOps;
        this.jsonToFuncConverterBuilder = jsonToFuncConverterBuilder;
    }

    @Override
    public JsonToFuncConverterMap build() {

        ImmutableMap.Builder<String, JsonToFuncConverter> builder = ImmutableMap.builder();

        builder.put(OpNames.and, jsonToFuncConverterBuilder.arrayOp(logicalOps::and));
        builder.put(OpNames.or, jsonToFuncConverterBuilder.arrayOp(logicalOps::or));

        builder.put(OpNames.not, jsonToFuncConverterBuilder.op1(logicalOps::not));

        builder.put(OpNames.eq, jsonToFuncConverterBuilder.op2(comparisionOps::eq));
        builder.put(OpNames.ne, jsonToFuncConverterBuilder.op2(comparisionOps::ne));
        builder.put(OpNames.lt, jsonToFuncConverterBuilder.op2(comparisionOps::lt));
        builder.put(OpNames.lte, jsonToFuncConverterBuilder.op2(comparisionOps::lte));
        builder.put(OpNames.gt, jsonToFuncConverterBuilder.op2(comparisionOps::gt));
        builder.put(OpNames.gte, jsonToFuncConverterBuilder.op2(comparisionOps::gte));

        builder.put(OpNames.count, jsonToFuncConverterBuilder.op1(functionalOps::count));
        builder.put(OpNames.distinct, jsonToFuncConverterBuilder.op1(functionalOps::distinct));

        builder.put(OpNames.in, (jsonObject, converterMap) -> arrayOps.in(
            jsonToFuncConverterBuilder.toFunc(
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
            funcs[i] = jsonToFuncConverterBuilder.toFunc(
                args.getValue(i),
                converterMap
            );
        }
        return funcs;
    }
}
