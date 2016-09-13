package elasta.composer;

import elasta.core.promise.impl.Promises;
import elasta.core.statemachine.StateMachine;
import elasta.core.statemachine.StateMachineBuilder;
import elasta.module.ModuleSystem;
import elasta.webutils.EventAddresses;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import static elasta.core.statemachine.StateEntry.on;
import static elasta.core.statemachine.StateMachine.next;

/**
 * Created by Jango on 9/14/2016.
 */
public class RegisterStateMachines {
    public void register(ModuleSystem moduleSystem) {

        moduleSystem.export(StateMachineBuilder.class, EventAddresses.CREATE, module -> {
            module.export(
                StateMachine.builder()
                    .when(StateCnst.START, next(StateCnst.VALIDATE))
                    .when(StateCnst.VALIDATE, next(StateCnst.CREATE), on(StateCnst.VALIDATION_FAIL, StateCnst.VALIDATION_ERROR))
                    .when(StateCnst.VALIDATION_ERROR, next(StateCnst.END))
                    .when(StateCnst.CREATE, next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.VALIDATE, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.VALIDATION_ERROR, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.CREATE, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.START, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.END, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))
            );
        });

        moduleSystem.export(StateMachineBuilder.class, EventAddresses.FIND_ALL, module -> {

            module.export(
                StateMachine.builder()
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
                    })));

        });

        moduleSystem.export(StateMachineBuilder.class, EventAddresses.FIND, module -> {


            module.export(
                StateMachine.builder()
                    .when(StateCnst.START, StateMachine.next(StateCnst.FIND))
                    .when(StateCnst.FIND, StateMachine.next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.FIND, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(
                            new JsonObject()
                        ));
                    }))
                    .handlers(StateCnst.START, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))
                    .handlers(StateCnst.END, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))
            );
        });


        moduleSystem.export(StateMachineBuilder.class, EventAddresses.UPDATE_ALL_PROPERTIES, module -> {

            module.export(
                StateMachine.builder()
                    .when(StateCnst.START, next(StateCnst.VALIDATE))
                    .when(StateCnst.VALIDATE, next(StateCnst.UPDATE_ALL_PROPERTIES), on(StateCnst.VALIDATION_FAIL, StateCnst.VALIDATION_ERROR))
                    .when(StateCnst.VALIDATION_ERROR, next(StateCnst.END))
                    .when(StateCnst.UPDATE_ALL_PROPERTIES, next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.VALIDATE, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.VALIDATION_ERROR, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.UPDATE_ALL_PROPERTIES, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.START, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))

                    .handlers(StateCnst.END, StateMachine.execStart(val -> {
                        return Promises.just(StateMachine.triggerNext(val));
                    }))
            );
        });

        moduleSystem.export(StateMachineBuilder.class, EventAddresses.UPDATE_SOME_PROPERTIES, module -> {

            module.export(
                StateMachine.builder()
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
            );
        });

        moduleSystem.export(StateMachineBuilder.class, EventAddresses.DELETE, module -> {

            module.export(
                StateMachine.builder()
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
            );
        });
    }
}
