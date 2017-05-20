package test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.ComposerUtils;
import elasta.composer.Msg;
import elasta.composer.builder.impl.ConvertersMapBuilderImpl;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.builder.impl.UpdateMessageHandlerBuilderImpl;
import elasta.composer.model.response.builder.impl.AuthorizationErrorModelBuilderImpl;
import elasta.composer.model.response.builder.impl.ValidationErrorModelBuilderImpl;
import elasta.composer.model.response.builder.impl.ValidationResultModelBuilderImpl;
import elasta.core.promise.impl.Promises;
import elasta.eventbus.impl.SimpleEventBusImpl;
import elasta.pipeline.util.MessageBundleImpl;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/20/2017.
 */
public class UpdateTest {
    static final Vertx vertx = Vertx.vertx(
        new VertxOptions()
            .setEventLoopPoolSize(1)
            .setWorkerPoolSize(1)
            .setBlockedThreadCheckInterval(10000000)
            .setClustered(false)
    );

    public static void main(String[] asdf) {
        final JsonObjectMessageHandler messageHandler = new UpdateMessageHandlerBuilderImpl(
            o -> o,
            "post.employee.update",
            new SimpleEventBusImpl(
                vertx.eventBus()
            ),
            "employee",
            OrmTest.orm(vertx),
            val -> Promises.of(ImmutableList.of()),
            new ValidationErrorModelBuilderImpl(
                "validate.error",
                new MessageBundleImpl(
                    "{}"
                ),
                new ValidationResultModelBuilderImpl(
                    new MessageBundleImpl("{}")
                )
            ),
            new ConvertersMapBuilderImpl().build(),
            params -> Promises.of(true),
            "employee.update",
            new AuthorizationErrorModelBuilderImpl(
                "asldfj", new MessageBundleImpl("{}")
            ),
            flow -> message -> flow.<Msg>start(
                ComposerUtils.toMsg(message, "anonymous")
            ).err(throwable -> message.fail(12, "RequestProcessingFailed"))
                .then(msg -> System.out.println("GOT_MSG"))
                .then(msg -> message.reply(msg.body()))
                .err(Throwable::printStackTrace)
        ).build();

        vertx.eventBus().consumer("employee.update", messageHandler);

        vertx.eventBus().consumer("post.employee.update", message -> {
            System.out.println("post.employee.update -> " + message.body());
        });

        new SimpleEventBusImpl(vertx.eventBus()).sendAndReceive("employee.update", new JsonObject(
            ImmutableMap.of(
                "eid", 656656365,
                "ename", "sohan khan"
            )
        )).err(Throwable::printStackTrace)
            .then(objectMessage -> {
                System.out.println("Handler Response: " + objectMessage.body());
            });
    }
}
