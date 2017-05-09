package elasta.webutils;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 2016-11-18.
 */
public interface QueryStringConverter<T> {
    T convert(MultiMap params);
}
