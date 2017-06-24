package elasta.webutils;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import elasta.core.promise.impl.Promises;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Created by Jango on 11/17/2016.
 */
public interface Main {
    static void main(String[] args) throws Exception {

        JsonArray jsonArray = new JsonArray(Files.toString(new File("D:\\NetBeansProjects\\tracker-web-panel\\positions.json"), StandardCharsets.UTF_8));

        JsonArray array = new JsonArray();
        for (int i = 0; i < jsonArray.size(); i++) {
            JsonObject jsonObject = jsonArray.getJsonObject(i);

            JsonArray coordinates = jsonObject.getJsonObject("geometry").getJsonArray("coordinates");

            array.add(
                ImmutableMap.of(
                    "lat", coordinates.getDouble(1),
                    "lng", coordinates.getDouble(0)
                )
            );
        }

        Files.write(array.encodePrettily(), new File("D:\\NetBeansProjects\\tracker-web-panel\\positions.json"), StandardCharsets.UTF_8);

        System.out.println(array);
    }

    static void test () {
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
