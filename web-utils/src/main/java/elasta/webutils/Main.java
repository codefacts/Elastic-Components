package elasta.webutils;

import elasta.core.promise.impl.Promises;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/17/2016.
 */
public interface Main {
    static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        final Router router = Router.router(vertx);

        vertx.createHttpServer().requestHandler(router::accept).listen(85);
        System.out.println("started");

        vertx.eventBus().addInterceptor(event -> {
        });
        vertx.eventBus().consumer("", message -> {
        });

        vertx.eventBus().publisher(null);
        Promises.empty().filter(o -> true).then(o -> System.out.println("passed"));
    }
}
