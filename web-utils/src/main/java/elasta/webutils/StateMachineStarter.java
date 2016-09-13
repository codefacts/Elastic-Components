package elasta.webutils;

import elasta.core.promise.intfs.Promise;
import elasta.core.statemachine.StateMachine;
import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 9/14/2016.
 */
@FunctionalInterface
public interface StateMachineStarter<R> {
    Promise<R> startMachine(StateMachine machine, JsonObject body, MultiMap headers, String address, String replyAddress);
}
