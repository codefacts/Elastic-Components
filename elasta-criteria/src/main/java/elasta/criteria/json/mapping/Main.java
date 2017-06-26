package elasta.criteria.json.mapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.criteria.funcs.FieldFunc;
import elasta.criteria.json.mapping.impl.*;
import elasta.module.MutableModuleSystem;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
public interface Main {
    static void main(String[] args) {
        MutableModuleSystem mutableModuleSystem = MutableModuleSystem.create();

        mutableModuleSystem.export(JsonQueryParser.class, module -> {
            module.export(new JsonQueryParserImpl(
                new JsonToFuncConverterMapImpl(
                    ImmutableMap.<String, JsonToFuncConverter>builder()
                        .put("column", (jsonObject, converterMap) -> new FieldFunc(jsonObject.getString("arg")))
                        .putAll(
                            module.require(JsonToFuncConverterMap.class).getMap()
                        )
                        .build()
                ),
                new GenericJsonToFuncConverterImpl()
            ));
        });

        mutableModuleSystem.export(JsonToFuncConverterMap.class, module -> {

            final JsonToFuncConverterBuilderHelperImpl jsonToFuncConverterBuilderHelper = new JsonToFuncConverterBuilderHelperImpl(
                new ValueHolderOperationBuilderHelperImpl()
            );

            module.export(
                new JsonToFuncConverterMapBuilderImpl(
                    new JsonToFuncConverterHelperImpl(
                        jsonToFuncConverterBuilderHelper
                    ),
                    jsonToFuncConverterBuilderHelper
                ).build()
            );
        });

        mutableModuleSystem.export(ValueHolderOperationBuilderHelper.class, module -> module.export(
            new ValueHolderOperationBuilderHelperImpl()
        ));

        JsonQueryParser parser = mutableModuleSystem.require(JsonQueryParser.class);

        String sql = parser.toSql(
            new JsonObject(
                ImmutableMap.of(
                    "op", "and",
                    "args", ImmutableList.of(
                        ImmutableMap.of(
                            "op", "eq",
                            "arg1", ImmutableMap.of(
                                "op", "column",
                                "arg", "name"
                            ),
                            "arg2", "sohan"
                        ),
                        ImmutableMap.of(
                            "op", "gte",
                            "arg1", ImmutableMap.of(
                                "op", "column",
                                "arg", "age"
                            ),
                            "arg2", 18
                        ),
                        ImmutableMap.of(
                            "op", "lte",
                            "arg1", ImmutableMap.of(
                                "op", "column",
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
                                            "op", "column",
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
                                                "op", "column",
                                                "arg", "firstName"
                                            ),
                                            "arg2", "kona"
                                        ),
                                        ImmutableMap.of(
                                            "op", "gte",
                                            "arg1", ImmutableMap.of(
                                                "op", "column",
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
