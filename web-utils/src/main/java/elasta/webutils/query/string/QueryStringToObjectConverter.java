package elasta.webutils.query.string;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 2016-11-18.
 */
public interface QueryStringToObjectConverter<T> {
    T convert(MultiMap params);
}
