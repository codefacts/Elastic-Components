package elasta.webutils.app;

import elasta.commons.NestedMapBuilder;
import elasta.commons.NestedMapBuilderImpl;
import elasta.commons.Utils;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 2016-11-19.
 */
public class QueryStringToJsonObjectConverterImpl implements QueryStringToJsonObjectConverter {
    @Override
    public JsonObject convert(MultiMap params) {
        NestedMapBuilder<Object> builder = new NestedMapBuilderImpl<>();

        params.names().forEach(name -> {

            List<String> path = toPath(name);

            if (path.isEmpty()) {
                return;
            }

            List<String> list = params.getAll(name);

            if (list.isEmpty()) {
                return;
            }

            if (list.size() == 1) {
                builder.put(path, list.get(0));
                return;
            }

            builder.put(path, new ArrayList<>(list));
        });

        return new JsonObject(builder.build());
    }

    private List<String> toPath(String name) {
        return Arrays.asList(name.split("\\.")).stream().map(String::trim).filter((s) -> not(s.isEmpty())).collect(Collectors.toList());
    }
}
