package elasta.criteria.json.mapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.criteria.funcs.ops.impl.*;
import elasta.criteria.json.mapping.impl.*;
import elasta.module.ModuleSystem;
import elasta.criteria.funcs.ops.ComparisionOps;
import elasta.criteria.funcs.ops.ValueHolderOps;
import elasta.criteria.funcs.ops.LogicalOps;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
public interface Main {
    static void main(String[] args) {
        ModuleSystem moduleSystem = ModuleSystem.create();

        moduleSystem.export(JsonQueryParser.class, module -> {
            module.export(new JsonQueryParserImpl(
                new JsonToFuncConverterMapImpl(
                    ImmutableMap.<String, JsonToFuncConverter>builder()
                        .put("field", (jsonObject, converterMap) -> paramsBuilder -> jsonObject.getString("arg"))
                        .putAll(
                            module.require(JsonToFuncConverterMap.class).getMap()
                        )
                        .build()
                ),
                new GenericJsonToFuncConverterImpl()
            ));
        });

        moduleSystem.export(JsonToFuncConverterMap.class, module -> {

            module.export(
                new JsonToFuncConverterMapBuilderImpl(
                    module.require(LogicalOps.class),
                    module.require(ComparisionOps.class),
                    module.require(ValueHolderOps.class),
                    new ArrayOpsImpl(),
                    new StringOpsImpl(),
                    new FunctionalOpsImpl(),
                    new JsonToFuncConverterBuilderImpl(
                        new ValueHolderOperationBuilderImpl(
                            module.require(ValueHolderOps.class)
                        )
                    )
                ).build()
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
                            "arg1", ImmutableMap.of(
                                "op", "field",
                                "arg", "name"
                            ),
                            "arg2", "sohan"
                        ),
                        ImmutableMap.of(
                            "op", "gte",
                            "arg1", ImmutableMap.of(
                                "op", "field",
                                "arg", "age"
                            ),
                            "arg2", 18
                        ),
                        ImmutableMap.of(
                            "op", "lte",
                            "arg1", ImmutableMap.of(
                                "op", "field",
                                "arg", "salary"
                            ),
                            "arg2", 50000
                        ),
                        ImmutableMap.of(
                            "op", "or",
                            "args", ImmutableList.of(
                                ImmutableMap.of(
                                    "op", "not",
                                    "arg", ImmutableMap.of(
                                        "op", "eq",
                                        "arg1", ImmutableMap.of(
                                            "op", "field",
                                            "arg", "name"
                                        ),
                                        "arg2", "kana"
                                    )
                                ),
                                ImmutableMap.of(
                                    "op", "and",
                                    "args", ImmutableList.of(
                                        ImmutableMap.of(
                                            "op", "eq",
                                            "arg1", ImmutableMap.of(
                                                "op", "field",
                                                "arg", "firstName"
                                            ),
                                            "arg2", "kona"
                                        ),
                                        ImmutableMap.of(
                                            "op", "gte",
                                            "arg1", ImmutableMap.of(
                                                "op", "field",
                                                "arg", "degree"
                                            ),
                                            "arg2", "Bsc"
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            ),
            value -> "?"
        );

        System.out.println(sql);

    }
}
