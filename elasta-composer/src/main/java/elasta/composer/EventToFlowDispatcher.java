package elasta.composer;

import elasta.core.eventbus.Intercepetor;
import elasta.core.eventbus.IntercepetorP;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 11/13/2016.
 */
public interface EventToFlowDispatcher extends IntercepetorP<JsonObject, JsonObject> {
    String EVENT = "$EVENT".toLowerCase();
    String ACTION = "$ACTION".toLowerCase();
    String ENTITY = "$ENTITY".toLowerCase();
}
