package elasta.composer.state.handlers;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.model.request.QueryModel;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 7/9/2017.
 */
final public class BeforeFindAllStateHandlerImpl implements BeforeFindAllStateHandler {
    @Override
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {
        return Promises.of(
            Flow.trigger(Events.next, msg)
        );
    }
}
