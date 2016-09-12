package elasta.core;

import elasta.core.promise.impl.Promises;
import elasta.core.statemachine.StateMachine;

import static elasta.core.statemachine.StateEntry.on;

/**
 * Created by Jango on 9/10/2016.
 */
public class StateMachineTest {
    public static void main(String[] args) {

        test1();

    }

    private static void test1() {

        StateMachine.builder()
            .when("start",
                on("ok", "respond"),
                on("err", "errState"),
                on("fuck", "fuckState"))
            .handlers("errState", StateMachine.exec(o -> {
                System.out.println("errState");
                return Promises.just(StateMachine.exit("errState"));
            }, null))
            .handlers("fuckState", StateMachine.exec(null, () -> {
                System.out.println("fuckState: end");
                return Promises.empty();
            }))
            .handlers("respond", StateMachine.exec(t -> {
                System.out.println("res");
                return Promises.just(StateMachine.exit());
            }, () -> {
                System.out.println("res:end");
                return Promises.empty();
            }))
            .handlers("start", StateMachine.exec(o -> {
                System.out.println("ok gury");
                return Promises.just(StateMachine.<String>trigger("fuck", "ok"));
            }, () -> {
                System.out.println("start: end");
                return Promises.empty();
            }))
            .startPoint("start")
            .build().start("start", "msg")
            .then(val -> System.out.println("got: " + val))
            .err(e -> e.printStackTrace());
    }
}
