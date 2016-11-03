package elasta.core;

import elasta.core.promise.impl.Promises;
import elasta.core.flow.Flow;

import static elasta.core.flow.EventToStateMapping.on;

/**
 * Created by Jango on 9/10/2016.
 */
public class FlowTest {
    public static void main(String[] args) {

        test1();

    }

    private static void test1() {

        Flow.builder()
            .when("start",
                on("ok", "respond"),
                on("err", "errState"),
                on("fuck", "fuckState"))
            .exec("errState", Flow.exec(o -> {
                System.out.println("errState");
                return Promises.just(Flow.exit("errState"));
            }, null))
            .exec("fuckState", Flow.exec(null, () -> {
                System.out.println("fuckState: end");
                return Promises.empty();
            }))
            .exec("respond", Flow.exec(t -> {
                System.out.println("res");
                return Promises.just(Flow.exit());
            }, () -> {
                System.out.println("res:end");
                return Promises.empty();
            }))
            .exec("start", Flow.exec(o -> {
                System.out.println("ok gury");
                return Promises.just(Flow.<String>trigger("fuck", "ok"));
            }, () -> {
                System.out.println("start: end");
                return Promises.empty();
            }))
            .initialState("start")
            .build().start("start", "msg")
            .then(val -> System.out.println("got: " + val))
            .err(e -> e.printStackTrace());
    }
}
