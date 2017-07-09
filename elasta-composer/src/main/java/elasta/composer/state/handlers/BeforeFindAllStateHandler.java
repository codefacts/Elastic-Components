package elasta.composer.state.handlers;

import elasta.composer.Msg;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/9/2017.
 */
public interface BeforeFindAllStateHandler extends MsgEnterEventHandlerP<JsonObject, JsonObject> {
    @Override
    Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable;
}
