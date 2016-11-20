package elasta.webutils.app;

import elasta.pipeline.util.NestedMapBuilder;
import elasta.pipeline.util.NestedMapBuilderImpl;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 2016-11-19.
 */
public class QueryStringToJsonObjectConverterImpl implements QueryStringToJsonObjectConverter {
    @Override
    public JsonObject convert(MultiMap params) {
        NestedMapBuilder<Object> builder = new NestedMapBuilderImpl<>();

        params.names().forEach(name -> {

            List<String> list = params.getAll(name);

            if (list.isEmpty()) {
                return;
            }

            name = name.trim();

            if (name.endsWith("[]")) {
                builder.put(name, list);
            }

            builder.put(name, list.get(0));
        });

        return new JsonObject(builder.build());
    }
}
