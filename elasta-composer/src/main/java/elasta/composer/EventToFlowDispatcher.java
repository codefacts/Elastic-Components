package elasta.composer;

import elasta.core.eventbus.ProcessorP;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 11/13/2016.
 */
public interface EventToFlowDispatcher extends ProcessorP<JsonObject, JsonObject> {
    String EVENT = "$EVENT".toLowerCase();
    String ACTION = "$ACTION".toLowerCase();
    String RESOURCE = "$RESOURCE".toLowerCase();
}
