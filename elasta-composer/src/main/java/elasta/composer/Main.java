package elasta.composer;

import elasta.core.flow.Flow;
import io.vertx.core.Vertx;

/**
 * Created by sohan on 5/10/2017.
 */
public interface Main {
    static void main(String[] asdf) {
        System.out.println("ok");
        Vertx.vertx().eventBus().consumer("", event -> {});
    }
}
