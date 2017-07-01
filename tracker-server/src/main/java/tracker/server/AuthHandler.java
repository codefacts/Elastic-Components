package tracker.server;

import com.google.common.collect.ImmutableMap;
import elasta.composer.MessageBus;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import tracker.Addresses;
import tracker.entity_config.Entities;
import tracker.model.UserModel;

import java.util.Objects;

/**
 * Created by sohan on 7/1/2017.
 */
final public class AuthHandler implements RequestHandler {
    final Vertx vertx;
    final MessageBus messageBus;

    public AuthHandler(Vertx vertx, MessageBus messageBus) {
        Objects.requireNonNull(vertx);
        Objects.requireNonNull(messageBus);
        this.vertx = vertx;
        this.messageBus = messageBus;
    }

    @Override
    public void handle(RoutingContext ctx) {

        JsonObject req = ctx.getBodyAsJson();

        Objects.requireNonNull(req);

        messageBus.sendAndReceiveJsonObject(
            MessageBus.Params.builder()
                .address(Addresses.findAll(Entities.USER))
                .message(
                    
                    new JsonObject(

                        ImmutableMap.of(
                            UserModel.userId, req.getString(UserModel.userId),
                            UserModel.password, req.getString(UserModel.password)
                        )
                    )
                )
                .build()
        ).then(message -> {

            JsonObject user = message.body();


        }).err(ctx::fail);
    }
}
