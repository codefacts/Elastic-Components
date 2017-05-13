package elasta.pipeline;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/14/2017.
 */
public interface MessageBundle {

    String translate(String messageCode, JsonObject json);
}
