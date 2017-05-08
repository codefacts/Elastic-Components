package elasta.orm.impl;

import com.google.common.collect.ImmutableMap;
import elasta.orm.entity.EntityMappingHelper;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 5/8/2017.
 */
public interface OperatorUtils {

    static <T> JsonObject eq(String fieldExpStr, T id) {
        return new JsonObject(
            ImmutableMap.of(
                "op", "eq",
                "arg1", ImmutableMap.of(
                    "op", "field",
                    "arg", fieldExpStr
                ),
                "arg2", id
            )
        );
    }

    static JsonObject countDistinct(String fieldExpStr) {
        return new JsonObject(
            ImmutableMap.of(
                "op", "count",
                "arg", ImmutableMap.of(
                    "op", "distinct",
                    "arg", ImmutableMap.of(
                        "op", "field",
                        "arg", fieldExpStr
                    )
                )
            )
        );
    }

    static <T> JsonObject in(String fieldExpStr, Collection<T> ids) {
        return new JsonObject(
            ImmutableMap.of(
                "op", "in",
                "arg1", ImmutableMap.of(
                    "op", "field",
                    "arg", fieldExpStr
                ),
                "args", ids
            )
        );
    }
}
