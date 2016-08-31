package elasta.composer.pipelines;

import io.crm.transformation.Transform;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by shahadat on 3/6/16.
 */
public class PathTransform<R> implements Transform<JsonObject, List<R>> {
    private Map<String[], Function<?, R>> map = new LinkedHashMap<>();

    public PathTransform<R> root(Function<JsonObject, R> function) {
        map.put(null, function);
        return this;
    }

    public PathTransform<R> object(String[] path, Function<JsonObject, R> function) {
        map.put(path, function);
        return this;
    }

    public PathTransform<R> array(String[] path, Function<JsonArray, R> function) {
        map.put(path, function);
        return this;
    }

    public static void main(String... args) {
        System.out.println(new LinkedHashMap<>().put(null, null));
        System.out.println();
    }

    @Override
    public List<R> transform(JsonObject val) {
        ArrayList<R> list = new ArrayList<>();
        map.forEach((path, function) -> {
            R r = apply(val, path, (Function<Object, R>) function);
            list.add(r);
        });
        return list;
    }

    private R apply(JsonObject json, String[] path, Function<Object, R> function) {
        final int len = path.length - 1;
        for (int i = 0; i < len; i++) {
            json = json.getJsonObject(path[i]);
        }
        Object value = json.getValue(path[path.length - 1]);
        return function.apply(value);
    }
}
