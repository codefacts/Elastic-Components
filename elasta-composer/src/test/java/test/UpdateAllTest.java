package test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.ComposerUtils;
import elasta.composer.Msg;
import elasta.composer.builder.impl.ConvertersMapBuilderImpl;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.message.handlers.JsonObjectMessageHandler;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.impl.UpdateAllMessageHandlerBuilderImpl;
import elasta.composer.message.handlers.builder.impl.UpdateMessageHandlerBuilderImpl;
import elasta.composer.model.response.builder.impl.*;
import elasta.core.promise.impl.Promises;
import elasta.eventbus.impl.SimpleEventBusImpl;
import elasta.pipeline.util.MessageBundleImpl;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by sohan on 5/21/2017.
 */
final public class UpdateAllTest {
    static final Vertx vertx = Vertx.vertx(
        new VertxOptions()
            .setEventLoopPoolSize(1)
            .setWorkerPoolSize(1)
            .setBlockedThreadCheckInterval(10000000)
            .setClustered(false)
    );

    public static void main(String[] asdf) {
        MessageHandler<List<JsonObject>> messageHandler = new UpdateAllMessageHandlerBuilderImpl(
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
            new ValidationSuccessModelBuilderImpl(new MessageBundleImpl("{}")),
            new ConvertersMapBuilderImpl().build(),
            params -> Promises.of(true),
            "employee.update",
            new AuthorizationErrorModelBuilderImpl(
                "asldfj", new MessageBundleImpl("{}")
            ),
            new AuthorizationSuccessModelBuilderImpl(new MessageBundleImpl("{}"))
            ,
            buildHandler()
        ).build();

        vertx.eventBus().consumer("employee.update", messageHandler);

        vertx.eventBus().consumer("post.employee.update", message -> {
            System.out.println("post.employee.update -> " + message.body());
        });

        new SimpleEventBusImpl(vertx.eventBus()).sendAndReceive("employee.update",
            new JsonArray(
                ImmutableList.of(
                    new JsonObject(
                        ImmutableMap.of(
                            "eid", 686829552,
                            "ename", "sohan khamba"
                        )
                    ),
                    new JsonObject(
                        ImmutableMap.of(
                            "eid", 683163666,
                            "ename", "kona moni"
                        )
                    )
                )
            )
        ).err(Throwable::printStackTrace)
            .then(objectMessage -> {
                System.out.println("Handler Response: " + objectMessage.body());
            });
    }

    private static FlowToMessageHandlerConverter<List<JsonObject>> buildHandler() {
        return flow -> message -> flow.<Msg>start(
            ComposerUtils.toMsg(message, "anonymous")
        ).err(throwable -> message.fail(12, "RequestProcessingFailed"))
            .then(msg -> System.out.println("GOT_MSG"))
            .then(msg -> message.reply(msg.body()))
            .err(Throwable::printStackTrace);
    }
}
