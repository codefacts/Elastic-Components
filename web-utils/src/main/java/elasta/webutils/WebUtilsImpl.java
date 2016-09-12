package elasta.webutils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 9/12/2016.
 */
public class WebUtilsImpl implements WebUtils {

    public JsonObject toJson(MultiMap params) {
        ImmutableMap.Builder<String, Object> paramsMapBuilder = ImmutableMap.builder();

        for (String name : params.names()) {
            List<String> list = params.getAll(name);

            if (list.size() > 1) {

                ImmutableList.Builder<Object> listBuilder = ImmutableList.builder();

                list.forEach(listBuilder::add);

                paramsMapBuilder.put(name, listBuilder.build());

            } else {

                list.forEach(val -> paramsMapBuilder.put(name, val));

            }
        }

        return new JsonObject(paramsMapBuilder.build());
    }
}
