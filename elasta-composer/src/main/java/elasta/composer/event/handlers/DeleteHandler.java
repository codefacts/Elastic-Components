package elasta.composer.event.handlers;

import elasta.composer.StateCnst;
import elasta.core.promise.impl.Promises;
import elasta.core.statemachine.StateMachine;
import elasta.vertxutils.VertxUtils;
import elasta.webutils.StateMachineStarter;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import static elasta.core.statemachine.StateEntry.on;
import static elasta.core.statemachine.StateMachine.next;

/**
 * Created by Jango on 9/12/2016.
 */
public class DeleteHandler {
    final VertxUtils vertxUtils;
    private final StateMachineStarter stateMachineStarter;

    public DeleteHandler(VertxUtils vertxUtils, StateMachineStarter stateMachineStarter) {
        this.vertxUtils = vertxUtils;
        this.stateMachineStarter = stateMachineStarter;
    }

    public void delete(Message<JsonObject> message) {

        vertxUtils.handleMessage(message,
            (body, headers, address, replyAddress) -> {

                StateMachine machine = StateMachine.builder()
                    .when(StateCnst.START, next(StateCnst.VALIDATE))
                    .when(StateCnst.VALIDATE, next(StateCnst.DELETE), on(StateCnst.VALIDATION_FAIL, StateCnst.VALIDATION_ERROR))
                    .when(StateCnst.VALIDATION_ERROR, next(StateCnst.END))
                    .when(StateCnst.DELETE, next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.VALIDATE, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.VALIDATION_ERROR, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.DELETE, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.START, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.END, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))
                    .build();


                return stateMachineStarter.startMachine(machine, body, headers, address, replyAddress);
            });

    }
}
