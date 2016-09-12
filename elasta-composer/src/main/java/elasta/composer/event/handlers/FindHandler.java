package elasta.composer.event.handlers;

import elasta.composer.StateCnst;
import elasta.core.promise.impl.Promises;
import elasta.core.statemachine.StateMachine;
import elasta.vertxutils.VertxUtils;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 9/12/2016.
 */
public class FindHandler {
    private final VertxUtils vertxUtils;

    public FindHandler(VertxUtils vertxUtils) {
        this.vertxUtils = vertxUtils;
    }

    public void find(Message<JsonObject> message) {

        vertxUtils.handleMessage(message,
            (body, headers, address, replyAddress) -> {

                StateMachine machine = StateMachine.builder()
                    .when(StateCnst.START, StateMachine.next(StateCnst.FIND))
                    .when(StateCnst.FIND, StateMachine.next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.FIND, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(
                            new JsonObject().put("data", new JsonArray()))
                        );
                    }))
                    .handlers(StateCnst.START, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))
                    .handlers(StateCnst.END, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))
                    .build();

                return machine.start(message.body());
            });

    }
}
