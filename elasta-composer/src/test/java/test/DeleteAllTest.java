package test;

import com.google.common.collect.ImmutableList;
import elasta.composer.ComposerUtils;
import elasta.composer.ErrorCodes;
import elasta.composer.Msg;
import elasta.composer.builder.impl.ConvertersMapBuilderImpl;
import elasta.composer.converter.FlowToJsonArrayMessageHandlerConverter;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.impl.DeleteAllMessageHandlerBuilderImpl;
import elasta.composer.model.response.builder.impl.*;
import elasta.core.promise.impl.Promises;
import elasta.eventbus.impl.SimpleEventBusImpl;
import elasta.pipeline.util.MessageBundleImpl;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 5/21/2017.
 */
public class DeleteAllTest {
    static final Vertx vertx = Vertx.vertx(
        new VertxOptions()
            .setEventLoopPoolSize(1)
            .setWorkerPoolSize(1)
            .setBlockedThreadCheckInterval(10000000)
            .setClustered(false)
    );

    public static void main(String[] asdf) {
        MessageHandler<JsonArray> messageHandler = new DeleteAllMessageHandlerBuilderImpl(
            o -> o,
            new SimpleEventBusImpl(vertx.eventBus()),
            "post.employee.delete",
            OrmTest.orm(vertx),
            "employee",
            "employee.delete",
            params -> Promises.of(true),
            new AuthorizationErrorModelBuilderImpl(ErrorCodes.authorizationError, new MessageBundleImpl("{}")),
            new AuthorizationSuccessModelBuilderImpl(new MessageBundleImpl("{}")),
            new ConvertersMapBuilderImpl().build(),
            buildHandler(),
            sqlDB).build();

        vertx.eventBus().consumer("employee.delete", messageHandler);

        vertx.eventBus().consumer("post.employee.delete", message -> {
            System.out.println("post.employee.delete -> " + message.body());
        });

        //686829552, 683163666
        new SimpleEventBusImpl(vertx.eventBus()).sendAndReceive("employee.delete",
            new JsonArray(
                ImmutableList.of(
                    686829552,
                    683163666
                )
            )
        ).err(Throwable::printStackTrace)
            .then(objectMessage -> {
                System.out.println("Handler Response: " + objectMessage.body());
            });
    }

    private static FlowToJsonArrayMessageHandlerConverter buildHandler() {
        return flow -> message -> flow.<Msg>start(
            ComposerUtils.toMsg(message, "anonymous")
        ).err(throwable -> message.fail(12, "RequestProcessingFailed"))
            .then(msg -> System.out.println("GOT_MSG"))
            .then(msg -> message.reply(msg.body()))
            .err(Throwable::printStackTrace);
    }
}
