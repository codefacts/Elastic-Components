package tracker;

import com.google.common.collect.ImmutableMap;
import elasta.composer.MessageBus;
import elasta.module.ModuleSystem;
import elasta.module.ModuleSystemBuilder;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/29/2017.
 */
final public class AppTest {
    public static void main(String[] asfd) {

        ModuleSystemBuilder builder = ModuleSystem.builder();

        TrackerExporter.exportTo(
            TrackerExporter.ExportToParams.builder()
                .builder(builder)
                .config(
                    new App.Config(
                        new JsonObject(
                            ImmutableMap.of(
                                "user", "root",
                                "password", "",
                                "driver_class", "com.mysql.jdbc.Driver",
                                "url", "jdbc:mysql://localhost/tracker_db"
                            )
                        ),
                        ImmutableMap.of(),
                        Vertx.vertx()
                    )
                )
                .build()
        );

        ModuleSystem module = builder.build();

        createMessageHandlers(module);

        final MessageBus messageBus = module.require(MessageBus.class);

//        messageBus.sendAndReceive(
//            MessageBus.Params.builder()
//                .address(Addresses.userCreate)
//                .message(
//                    new JsonObject(
//                        "{\n" +
//                            "  \"userId\" : \"admin-5\",\n" +
//                            "  \"username\" : \"fakmin5\",\n" +
//                            "  \"email\" : \"fakmin@fakmin5\",\n" +
//                            "  \"phone\" : \"01951883417\"\n" +
//                            "}"
//                    )
//                )
//                .userId("admin-1")
//                .build()
//        ).then(objectMessage -> {
//            System.out.println(objectMessage.body());
//            System.out.println(objectMessage.headers());
//        });

        messageBus.sendAndReceiveJsonObject(
            MessageBus.Params.builder()
                .userId("admin-1")
                .address(Addresses.userFindOne)
                .message(new JsonObject(
                    ImmutableMap.of(
                        "userId", "admin-2"
                    )
                ))
                .build()
        ).then(message -> System.out.println(message.body()))
            .err(Throwable::printStackTrace);

    }

    private static ModuleSystem createMessageHandlers(ModuleSystem module) {

        EventBus eventBus = module.require(EventBus.class);

        module.require(MessageHandlersBuilder.class).build(module).forEach(addressAndHandler -> {
            eventBus.consumer(addressAndHandler.getAddress(), addressAndHandler.getMessageHandler());
        });

        return module;
    }
}
