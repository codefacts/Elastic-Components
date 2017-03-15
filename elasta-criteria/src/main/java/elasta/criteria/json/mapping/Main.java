package elasta.criteria.json.mapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.module.ModuleSystem;
import elasta.criteria.funcs.ops.ComparisionOps;import elasta.criteria.funcs.ops.ValueHolderOps;import elasta.criteria.funcs.ops.impl.ComparisionOpsImpl;import elasta.criteria.funcs.ops.impl.LogicalOpsImpl;import elasta.criteria.funcs.ops.impl.ValueHolderOpsImpl;import elasta.criteria.json.mapping.impl.JsonQueryParserImpl;import elasta.criteria.json.mapping.impl.JsonToFuncConverterRegistryImpl;import elasta.criteria.funcs.ops.ComparisionOps;
import elasta.criteria.funcs.ops.LogicalOps;
import elasta.criteria.funcs.ops.ValueHolderOps;
import elasta.criteria.funcs.ops.impl.ComparisionOpsImpl;
import elasta.criteria.funcs.ops.impl.LogicalOpsImpl;
import elasta.criteria.funcs.ops.impl.ValueHolderOpsImpl;
import elasta.criteria.json.mapping.impl.JsonQueryParserImpl;
import elasta.criteria.json.mapping.impl.JsonToFuncConverterRegistryHelper;
import elasta.criteria.json.mapping.impl.JsonToFuncConverterRegistryImpl;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
public interface Main {
    static void main(String[] args) {
        ModuleSystem moduleSystem = ModuleSystem.create();

        moduleSystem.export(JsonQueryParser.class, module -> {
            module.export(new JsonQueryParserImpl(module.require(JsonToFuncConverterRegistry.class)));
        });

        moduleSystem.export(JsonToFuncConverterRegistry.class, module -> {

            module.export(
                new JsonToFuncConverterRegistryImpl(
                    new JsonToFuncConverterRegistryHelper(
                        module.require(LogicalOps.class),
                        module.require(ComparisionOps.class),
                        module.require(ValueHolderOps.class),
                        module.require(ValueHolderOperationBuilder.class)
                    ).getConverterMap()
                )
            );
        });

        moduleSystem.export(LogicalOps.class, module -> module.export(new LogicalOpsImpl()));
        moduleSystem.export(ComparisionOps.class, module -> module.export(new ComparisionOpsImpl()));
        moduleSystem.export(ValueHolderOps.class, module -> module.export(new ValueHolderOpsImpl()));
        moduleSystem.export(ValueHolderOperationBuilder.class, module -> module.export(
            new ValueHolderOperationBuilderImpl(
                module.require(ValueHolderOps.class)
            )
        ));

        JsonQueryParser parser = moduleSystem.require(JsonQueryParser.class);

        String sql = parser.toSql(
            new JsonObject(
                ImmutableMap.of(
                    "op", "and",
                    "args", ImmutableList.of(
                        ImmutableMap.of(
                            "op", "eq",
                            "arg1", "name",
                            "arg2", "sohan"
                        ),
                        ImmutableMap.of(
                            "op", "gte",
                            "arg1", "age",
                            "arg2", 18
                        ),
                        ImmutableMap.of(
                            "op", "lte",
                            "arg1", "salary",
                            "arg2", 50000
                        ),
                        ImmutableMap.of(
                            "op", "or",
                            "args", ImmutableList.of(
                                ImmutableMap.of(
                                    "op", "not",
                                    "arg", ImmutableMap.of(
                                        "op", "eq",
                                        "arg1", "name",
                                        "arg2", "kana"
                                    )
                                ),
                                ImmutableMap.of(
                                    "op", "and",
                                    "args", ImmutableList.of(
                                        ImmutableMap.of(
                                            "op", "eq",
                                            "arg1", "firstName",
                                            "arg2", "kona"
                                        ),
                                        ImmutableMap.of(
                                            "op", "gte",
                                            "arg1", "degree",
                                            "arg2", "Bsc"
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            value -> value.toString()
        );

        System.out.println(sql);

    }
}
