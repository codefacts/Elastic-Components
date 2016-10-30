package elasta.composer;

import elasta.core.promise.impl.Promises;
import elasta.core.flow.Flow;
import elasta.core.flow.FlowBuilder;
import elasta.module.ModuleSystem;
import elasta.webutils.EventAddresses;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import static elasta.core.flow.FlowEntry.on;
import static elasta.core.flow.Flow.next;

/**
 * Created by Jango on 9/14/2016.
 */
public class RegisterStateMachines {
    public void register(ModuleSystem moduleSystem) {

        moduleSystem.export(FlowBuilder.class, EventAddresses.CREATE, module -> {
            module.export(
                Flow.builder()
                    .when(StateCnst.START, next(StateCnst.VALIDATE))
                    .when(StateCnst.VALIDATE, next(StateCnst.CREATE), on(EventCnst.VALIDATION_FAIL, StateCnst.VALIDATION_ERROR))
                    .when(StateCnst.VALIDATION_ERROR, next(StateCnst.END))
                    .when(StateCnst.CREATE, next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.VALIDATE, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.VALIDATION_ERROR, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.CREATE, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.START, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.END, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })
            );
        });

        moduleSystem.export(FlowBuilder.class, EventAddresses.FIND_ALL, module -> {

            module.export(
                Flow.builder()
                    .when(StateCnst.START, Flow.next(StateCnst.FIND_ALL))
                    .when(StateCnst.FIND_ALL, Flow.next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.FIND_ALL, val -> {
                        return Promises.just(Flow.triggerNext(
                            new JsonObject().put("data", new JsonArray()))
                        );
                    })
                    .handlers(StateCnst.START, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })
                    .handlers(StateCnst.END, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    }));

        });

        moduleSystem.export(FlowBuilder.class, EventAddresses.FIND, module -> {


            module.export(
                Flow.builder()
                    .when(StateCnst.START, Flow.next(StateCnst.FIND))
                    .when(StateCnst.FIND, Flow.next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.FIND, val -> {
                        return Promises.just(Flow.triggerNext(
                            new JsonObject()
                        ));
                    })
                    .handlers(StateCnst.START, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })
                    .handlers(StateCnst.END, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })
            );
        });


        moduleSystem.export(FlowBuilder.class, EventAddresses.UPDATE_ALL_PROPERTIES, module -> {

            module.export(
                Flow.builder()
                    .when(StateCnst.START, next(StateCnst.VALIDATE))
                    .when(StateCnst.VALIDATE, next(StateCnst.UPDATE_ALL_PROPERTIES), on(EventCnst.VALIDATION_FAIL, StateCnst.VALIDATION_ERROR))
                    .when(StateCnst.VALIDATION_ERROR, next(StateCnst.END))
                    .when(StateCnst.UPDATE_ALL_PROPERTIES, next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.VALIDATE, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.VALIDATION_ERROR, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.UPDATE_ALL_PROPERTIES, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.START, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.END, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })
            );
        });

        moduleSystem.export(FlowBuilder.class, EventAddresses.UPDATE_SOME_PROPERTIES, module -> {

            module.export(
                Flow.builder()
                    .when(StateCnst.START, next(StateCnst.VALIDATE))
                    .when(StateCnst.VALIDATE, next(StateCnst.UPDATE_SOME_PROPERTIES), on(EventCnst.VALIDATION_FAIL, StateCnst.VALIDATION_ERROR))
                    .when(StateCnst.VALIDATION_ERROR, next(StateCnst.END))
                    .when(StateCnst.UPDATE_SOME_PROPERTIES, next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.VALIDATE, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.VALIDATION_ERROR, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.UPDATE_SOME_PROPERTIES, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.START, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.END, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })
            );
        });

        moduleSystem.export(FlowBuilder.class, EventAddresses.DELETE, module -> {

            module.export(
                Flow.builder()
                    .when(StateCnst.START, next(StateCnst.VALIDATE))
                    .when(StateCnst.VALIDATE, next(StateCnst.DELETE), on(EventCnst.VALIDATION_FAIL, StateCnst.VALIDATION_ERROR))
                    .when(StateCnst.VALIDATION_ERROR, next(StateCnst.END))
                    .when(StateCnst.DELETE, next(StateCnst.END))

                    .startPoint(StateCnst.START)

                    .handlers(StateCnst.VALIDATE, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.VALIDATION_ERROR, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.DELETE, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.START, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })

                    .handlers(StateCnst.END, val -> {
                        return Promises.just(Flow.triggerNext(val));
                    })
            );
        });
    }
}
