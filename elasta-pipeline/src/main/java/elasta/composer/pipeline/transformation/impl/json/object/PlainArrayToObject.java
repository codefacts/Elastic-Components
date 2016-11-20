package elasta.composer.pipeline.transformation.impl.json.object;

import com.google.common.collect.ImmutableList;
import elasta.composer.pipeline.transformation.Transformation;
import elasta.composer.pipeline.util.SimpleCounter;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by shahadat on 3/8/16.
 */
public class PlainArrayToObject implements Transformation<JsonArray, JsonObject> {
    private final List<String> fields;

    public PlainArrayToObject(List<String> fields) {
        this.fields = ImmutableList.copyOf(fields);
    }

    @Override
    public JsonObject transform(JsonArray array) {

        if (array == null) return null;

        JsonObject js = new JsonObject();
        SimpleCounter counter = new SimpleCounter(0);
        fields.forEach(field -> js.put(field, array.getValue(counter.counter++)));
        return js;
    }
}
