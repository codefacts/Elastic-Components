package elasta.webutils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 9/10/2016.
 */
public interface WebUtils {
    JsonObject toJson(MultiMap params);
}
