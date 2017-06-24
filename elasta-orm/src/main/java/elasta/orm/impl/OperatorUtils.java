package elasta.orm.impl;

import com.google.common.collect.ImmutableMap;
import elasta.orm.entity.EntityMappingHelper;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/8/2017.
 */
public interface OperatorUtils {

    String field = "field";
    String op = "op";
    String arg = "arg";
    String args = "args";
    String arg1 = "arg1";
    String arg2 = "arg2";

    static JsonObject field(String fieldExpStr) {
        return new JsonObject(
            ImmutableMap.of(
                op, field,
                "arg", fieldExpStr
            )
        );
    }

    static <T> JsonObject eq(String fieldExpStr, T value) {
        return new JsonObject(
            ImmutableMap.of(
                op, "eq",
                "arg1", field(fieldExpStr),
                "arg2", value
            )
        );
    }

    static JsonObject countDistinct(String fieldExpStr) {
        return new JsonObject(
            ImmutableMap.of(
                op, "count",
                "arg", ImmutableMap.of(
                    op, "distinct",
                    "arg", field(fieldExpStr)
                )
            )
        );
    }

    static <T> JsonObject in(String fieldExpStr, Collection<T> ids) {
        return new JsonObject(
            ImmutableMap.of(
                op, "in",
                "arg1", field(fieldExpStr),
                "args", ids
            )
        );
    }

    static JsonObject and(List<JsonObject> criterias) {
        return new JsonObject(
            ImmutableMap.of(
                op, "and",
                "args", criterias
            )
        );
    }

    static JsonObject and(JsonArray criterias) {
        return new JsonObject(
            ImmutableMap.of(
                op, "and",
                "args", criterias
            )
        );
    }
}
