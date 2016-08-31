package elasta.composer.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.core.promise.intfs.Defer;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Shahadat on 8/31/2016.
 */
public class Util {

    public static <T> Handler<AsyncResult<T>> makeDeferred(Defer<T> defer) {
        return r -> {
            if (r.failed()) defer.reject(r.cause());
            else defer.resolve(r.result());
        };
    }

    public static <T> T as(Object obj, Class<T> tClass) {
        return tClass.cast(obj);
    }

    public static String parseCamelOrSnake(String src) {
        Pattern pattern = Pattern.compile("");
        String str = Stream.of(src).map(s -> s.replace('_', ' ')).map(s -> s.split(" "))
            .flatMap(strings -> Arrays.asList(strings).stream())
            .map(s -> splitCamelCase(s, " "))
            .flatMap(s -> Arrays.asList(s.split(" ")).stream())
            .map(String::trim)
            .map(s -> s.toCharArray())
            .map(chars -> accept(chars, chs -> {
                if (chs.length > 0)
                    chs[0] = Character.toUpperCase(chs[0]);
            }))
            .map(chars -> new String(chars))
            .collect(Collectors.joining(" "));
        ;
        return str;
    }

    public static String splitCamelCase(String s, String replace) {
        String regex = "([a-z])([A-Z])";
        String replacement = "$1" + replace + "$2";
        return s.replaceAll(regex, replacement);
    }

    public static JsonArray toImmutable(JsonArray jsonArray) {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();

        jsonArray.getList().forEach((v) -> {
            if (v instanceof JsonArray) {
                builder.add(toImmutable((JsonArray) v));
            } else if (v instanceof List) {
                builder.add(toImmutable(new JsonArray((List) v)));
            } else if (v instanceof JsonObject) {
                builder.add(toImmutable((JsonObject) v));
            } else if (v instanceof Map) {
                builder.add(toImmutable(new JsonObject((Map<String, Object>) v)));
            } else {
                builder.add(v);
            }
        });

        return new JsonArray(builder.build());
    }

    public static JsonObject toImmutable(JsonObject jsonObject) {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        jsonObject.getMap().forEach((k, v) -> {
            if (v instanceof JsonArray) {
                builder.put(k, toImmutable((JsonArray) v));
            } else if (v instanceof List) {
                builder.put(k, toImmutable(new JsonArray((List) v)));
            } else if (v instanceof JsonObject) {
                builder.put(k, toImmutable((JsonObject) v));
            } else if (v instanceof Map) {
                builder.put(k, toImmutable(new JsonObject((Map<String, Object>) v)));
            } else {
                builder.put(k, v);
            }
        });
        return new JsonObject(builder.build());
    }

    public static <T> T accept(final T t, final Consumer<T> consumer) {
        consumer.accept(t);
        return t;
    }
}
