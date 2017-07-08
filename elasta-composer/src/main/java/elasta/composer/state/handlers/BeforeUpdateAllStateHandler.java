package elasta.composer.state.handlers;

import elasta.composer.Msg;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 7/7/2017.
 */
public interface BeforeUpdateAllStateHandler extends MsgEnterEventHandlerP<JsonArray, JsonArray> {
    @Override
    Promise<StateTrigger<Msg<JsonArray>>> handle(Msg<JsonArray> msg) throws Throwable;
}
