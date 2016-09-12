package elasta.composer;

import elasta.core.promise.impl.Promises;
import elasta.core.statemachine.StateMachine;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 9/11/2016.
 */
final public class FindAllHandler {
    final App app;

    public FindAllHandler(App app) {
        this.app = app;
    }

    public void findAll(Message<JsonObject> message) {

        app.vertxUtils().handleMessage(message,
            (body, headers, address, replyAddress) -> {

                StateMachine machine = StateMachine.builder()
                    .when(StateCnst.START, StateMachine.next(StateCnst.FIND_ALL))
                    .when(StateCnst.FIND_ALL, StateMachine.next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.FIND_ALL, StateMachine.execStart(val -> {
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
