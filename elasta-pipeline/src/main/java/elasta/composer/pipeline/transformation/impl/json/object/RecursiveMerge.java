package elasta.composer.pipeline.transformation.impl.json.object;

import com.google.common.collect.ImmutableSet;
import elasta.composer.pipeline.transformation.Transformation;
import elasta.composer.pipeline.util.Util;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created by shahadat on 3/6/16.
 */
public class RecursiveMerge implements Transformation<JsonObject, JsonObject> {
    private final Set<List<String>> includes;
    private final Set<List<String>> excludes;
    private final Predicate<Object> predicate;
    private final BiConsumer<Map.Entry<String, Object>, Runnable> entryConsumer;
    private final Predicate<Object> arrayPredicate;
    private final BiConsumer<Object, Runnable> arrayValueConsumer;

    public RecursiveMerge(Set<List<String>> includes, Set<List<String>> excludes,
                          Predicate<Object> predicate,
                          BiConsumer<Map.Entry<String, Object>, Runnable> entryConsumer,
                          Predicate<Object> arrayPredicate,
                          BiConsumer<Object, Runnable> arrayValueConsumer) {
        this.includes = includes == null ? null : ImmutableSet.copyOf(includes);
        this.excludes = excludes == null ? null : ImmutableSet.copyOf(excludes);
        this.predicate = predicate;
        this.entryConsumer = entryConsumer;
        this.arrayPredicate = arrayPredicate;
        this.arrayValueConsumer = arrayValueConsumer;
    }

    @Override
    public JsonObject transform(JsonObject json) {

        if (json == null) return null;

        if (includes != null) {
            Stream<List<String>> stream = includes.stream();
            if (excludes != null && excludes.size() > 0) {
                stream = stream.filter(paths -> !excludes.contains(paths));
            }
            stream.forEach(paths -> apply(json, paths));
        } else {
            Iterator<Map.Entry<String, Object>> iterator = json.getMap().entrySet().iterator();
            if (excludes != null && excludes.size() > 0) {
                iterator.forEachRemaining(e -> {
                    List<String> strings = Arrays.asList(e.getKey());
                    Object value = e.getValue();
                    if (excludes.contains(strings)) return;
                    if (predicate.test(value)) entryConsumer.accept(e, iterator::remove);
                    else cleanRecursive(value, strings);
                });
            }
            iterator.forEachRemaining(e -> {
                Object value = e.getValue();
                if (predicate.test(value)) entryConsumer.accept(e, iterator::remove);
                else {
                    cleanRecursive(value);
                }
            });
        }

        return json;
    }

    private void cleanRecursive(Object value, List<String> strings) {
        if (value instanceof JsonObject) {
            Iterator<Map.Entry<String, Object>> iterator = ((JsonObject) value).getMap().entrySet().iterator();
            iterator.forEachRemaining(e -> {
                Object val = e.getValue();
                ArrayList<String> arrayList = Util.accept(new ArrayList<>(strings), list -> list.add(e.getKey()));
                if (excludes.contains(arrayList)) return;
                if (predicate.test(val)) entryConsumer.accept(e, iterator::remove);
                else cleanRecursive(val, arrayList);
            });
        } else if (value instanceof JsonArray) {
            Iterator iterator = ((JsonArray) value).getList().iterator();
            iterator.forEachRemaining(val -> {
                if (arrayPredicate.test(val)) arrayValueConsumer.accept(val, iterator::remove);
            });
        }
    }

    private void cleanRecursive(Object json) {
        if (json instanceof JsonObject) {
            Iterator<Map.Entry<String, Object>> iterator = ((JsonObject) json).getMap().entrySet().iterator();
            iterator.forEachRemaining(e -> {
                Object value = e.getValue();
                if (predicate.test(value)) {
                    entryConsumer.accept(e, iterator::remove);
                } else cleanRecursive(value);
            });
        } else if (json instanceof JsonArray) {
            Iterator<Object> iterator = ((JsonArray) json).getList().iterator();
            iterator.forEachRemaining(value -> {
                if (arrayPredicate.test(value)) {
                    arrayValueConsumer.accept(value, iterator::remove);
                } else cleanRecursive(value);
            });
        }
    }

    private void apply(JsonObject json, List<String> paths) {
        int len = paths.size() - 1;
        for (int i = 0; i < len; i++) {
            json = json.getJsonObject(paths.get(i));
        }
        clean(json.getValue(paths.get(paths.size() - 1)));
    }

    private void clean(Object json) {
        if (json instanceof JsonObject) {
            Iterator<Map.Entry<String, Object>> iterator = ((JsonObject) json).getMap().entrySet().iterator();
            iterator.forEachRemaining(e -> {
                if (predicate.test(e.getValue())) {
                    entryConsumer.accept(e, iterator::remove);
                }
            });
        } else if (json instanceof JsonArray) {
            Iterator<Object> iterator = ((JsonArray) json).getList().iterator();
            iterator.forEachRemaining(v -> {
                if (arrayPredicate.test(v)) {
                    arrayValueConsumer.accept(v, iterator::remove);
                }
            });
        }
    }
}
