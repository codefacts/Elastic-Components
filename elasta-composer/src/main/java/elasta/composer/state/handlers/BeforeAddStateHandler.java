package elasta.composer.state.handlers;

import elasta.composer.Msg;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/7/2017.
 */
public interface BeforeAddStateHandler extends MsgEnterEventHandlerP<JsonObject, JsonObject> {
    @Override
    Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable;
}
