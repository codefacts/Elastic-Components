package elasta.webutils.app;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/17/2016.
 */
public interface Main {
    static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        Router router = Router.router(vertx);

        vertx.createHttpServer().requestHandler(router::accept).listen(85);
        System.out.println("started");
    }
}
