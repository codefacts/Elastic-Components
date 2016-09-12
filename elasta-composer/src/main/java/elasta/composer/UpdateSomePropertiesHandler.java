package elasta.composer;

import elasta.core.promise.impl.Promises;
import elasta.core.statemachine.StateMachine;
import io.vertx.core.eventbus.Message;

import static elasta.core.statemachine.StateEntry.on;
import static elasta.core.statemachine.StateMachine.next;

/**
 * Created by Jango on 9/12/2016.
 */
public class UpdateSomePropertiesHandler {
    private final App app;

    public UpdateSomePropertiesHandler(App app) {
        this.app = app;
    }

    public void updateSomeProperties(Message message) {

        app.vertxUtils().handleMessage(message,
            (body, headers, address, replyAddress) -> {

                StateMachine machine = StateMachine.builder()
                    .when(StateCnst.START, next(StateCnst.VALIDATE))
                    .when(StateCnst.VALIDATE, next(StateCnst.UPDATE_SOME_PROPERTIES), on(StateCnst.VALIDATION_FAIL, StateCnst.VALIDATION_ERROR))
                    .when(StateCnst.VALIDATION_ERROR, next(StateCnst.END))
                    .when(StateCnst.UPDATE_SOME_PROPERTIES, next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.VALIDATE, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.VALIDATION_ERROR, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.UPDATE_SOME_PROPERTIES, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
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
