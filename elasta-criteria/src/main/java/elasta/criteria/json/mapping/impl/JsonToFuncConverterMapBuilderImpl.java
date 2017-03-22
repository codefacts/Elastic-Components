package elasta.criteria.json.mapping.impl;

import com.google.common.collect.ImmutableMap;
import elasta.criteria.funcs.ops.ComparisionOps;
import elasta.criteria.funcs.ops.LogicalOps;
import elasta.criteria.funcs.ops.ValueHolderOps;
import elasta.criteria.json.mapping.*;
import elasta.criteria.json.mapping.impl.JsonToFuncConverterMapImpl;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/20/2017.
 */
final public class JsonToFuncConverterMapBuilderImpl implements JsonToFuncConverterMapBuilder {
    final LogicalOps logicalOps;
    final ComparisionOps comparisionOps;
    final ValueHolderOps valueHolderOps;
    final JsonToFuncConverterBuilder jsonToFuncConverterBuilder;

    public JsonToFuncConverterMapBuilderImpl(LogicalOps logicalOps, ComparisionOps comparisionOps, ValueHolderOps valueHolderOps, JsonToFuncConverterBuilder jsonToFuncConverterBuilder) {
        Objects.requireNonNull(logicalOps);
        Objects.requireNonNull(comparisionOps);
        Objects.requireNonNull(valueHolderOps);
        Objects.requireNonNull(jsonToFuncConverterBuilder);
        this.logicalOps = logicalOps;
        this.comparisionOps = comparisionOps;
        this.valueHolderOps = valueHolderOps;
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

        return new JsonToFuncConverterMapImpl(
            builder.build()
        );
    }
}
