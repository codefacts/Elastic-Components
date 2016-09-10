package elasta.composer;

import elasta.core.intfs.FunctionUnchecked;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.core.statemachine.StateEntry;
import elasta.core.statemachine.StateMachine;
import elasta.core.statemachine.StateTrigger;
import io.vertx.core.Vertx;

/**
 * Created by Shahadat on 8/31/2016.
 */
public class App {

    public static void main(String[] args) {

//        final Vertx vertx = Vertx.vertx();

        StateMachine.builder()
                .when("start", StateEntry.on("ok", "respond"))
                .handlers("start", StateMachine.exec(new FunctionUnchecked<Object, Promise<StateTrigger<String>>>() {
                    @Override
                    public Promise<StateTrigger<String>> apply(Object o) throws Throwable {
                        System.out.println("ok gury");
                        return Promises.just(StateMachine.<String>trigger("ok", "ok"));
                    }
                }, () -> null))
                .setInitialState("start")
                .build().start("start", "msg");
    }
}
