package tracker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.MessageBus;
import elasta.module.ModuleSystem;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import tracker.entity_config.Entities;
import tracker.impl.AppImpl;
import tracker.model.UserModel;

import java.io.File;

/**
 * Created by sohan on 6/29/2017.
 */
final public class AppTest {

    public static void main(String[] asdf) {

        MessageBus messageBus = new AppImpl(
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
                Vertx.vertx(),
                1,
                10,
                "r",
                "",
                12
            )
        ).mesageBus();

        authTest(messageBus);

    }

    private static void authTest(MessageBus messageBus) {
        messageBus.sendAndReceiveJsonObject(
            MessageBus.Params.builder()
                .address(Addresses.authenticate)
                .message(
                    new JsonObject(
                        ImmutableMap.of(
                            UserModel.username, "fakmin11",
                            UserModel.password, "5151"
                        )
                    )
                )
                .userId(TrackerUtils.anonymous)
                .build()
        ).then(message -> {
            System.out.println(message.body());
        }).err(Throwable::printStackTrace);
    }

    private static void deleteAll(MessageBus messageBus) {
        messageBus.sendAndReceive(
            MessageBus.Params.builder()
                .address(Addresses.deleteAll(Entities.USER_ENTITY))
                .message(new JsonArray(
                    ImmutableList.of(
                        5640228953490447124L,
                        6462590966668780434L
                    )
                ))
                .userId("admoihasd")
                .build()
        ).then(objectMessage -> System.out.println(objectMessage.body()))
            .err(Throwable::printStackTrace);
    }

    private static void updateAll(MessageBus messageBus) {
        messageBus.sendAndReceive(
            MessageBus.Params.builder()
                .address(Addresses.updateAll(Entities.USER_ENTITY))
                .message(new JsonArray(ImmutableList.of(
                    new JsonObject(
                        "{\n" +
                            "  \"id\" : 5640228953490447124,\n" +
                            "  \"userId\" : \"admin-88\",\n" +
                            "  \"username\" : \"fakmin88\",\n" +
                            "  \"email\" : \"fakmin@fakmin88\",\n" +
                            "  \"phone\" : \"0195188357\"\n" +
                            "}"
                    ),
                    new JsonObject(
                        "{\n" +
                            "  \"id\" : 6462590966668780434,\n" +
                            "  \"userId\" : \"admin-90\",\n" +
                            "  \"username\" : \"fakmin90\",\n" +
                            "  \"email\" : \"fakmin@fakmin90\",\n" +
                            "  \"phone\" : \"01951883477\"\n" +
                            "}"
                    )
                )))
                .userId("adf")
                .build()
        ).then(objectMessage -> System.out.println(objectMessage.body()))
            .err(Throwable::printStackTrace);
    }

    private static void createAll(MessageBus messageBus) {
        messageBus.sendAndReceive(
            MessageBus.Params.builder()
                .address(Addresses.addAll(Entities.USER_ENTITY))
                .message(new JsonArray(ImmutableList.of(
                    new JsonObject(
                        "{\n" +
                            "  \"userId\" : \"admin-8\",\n" +
                            "  \"username\" : \"fakmin8\",\n" +
                            "  \"email\" : \"fakmin@fakmin8\",\n" +
                            "  \"phone\" : \"0195188348\"\n" +
                            "}"
                    ),
                    new JsonObject(
                        "{\n" +
                            "  \"userId\" : \"admin-9\",\n" +
                            "  \"username\" : \"fakmin9\",\n" +
                            "  \"email\" : \"fakmin@fakmin9\",\n" +
                            "  \"phone\" : \"01951883419\"\n" +
                            "}"
                    )
                )))
                .userId("adf")
                .build()
        ).then(objectMessage -> System.out.println(objectMessage.body()))
            .err(Throwable::printStackTrace);
    }

    private static void delete(MessageBus messageBus) {
        messageBus.sendAndReceive(
            MessageBus.Params.builder()
                .address(Addresses.delete(Entities.USER_ENTITY))
                .message(6788965377253291803L)
                .userId("admin-55")
                .build()
        ).then(objectMessage -> System.out.println(objectMessage.body()))
            .err(Throwable::printStackTrace);
    }

    private static void update(ModuleSystem module, MessageBus messageBus) {
        messageBus.sendAndReceive(
            MessageBus.Params.builder()
                .address(Addresses.update(Entities.USER_ENTITY))
                .message(
                    new JsonObject(
                        "{\n" +
                            "  \"id\" : 1,\n" +
                            "  \"userId\" : \"admin-11\",\n" +
                            "  \"username\" : \"fakmin11\",\n" +
                            "  \"email\" : \"fakmin@fakmin11\",\n" +
                            "  \"phone\" : \"01951883411\"\n" +
                            "}"
                    )
                )
                .userId("admin-1")
                .build()
        ).then(objectMessage -> {
            System.out.println(objectMessage.body());
            System.out.println(objectMessage.headers());
        });
    }

    private static void findAll(ModuleSystem module, MessageBus messageBus) {
        messageBus.sendAndReceiveJsonObject(
            MessageBus.Params.builder()
                .address(Addresses.findAll(Entities.USER_ENTITY))
                .message(new JsonObject())
                .userId("admin-1")
                .build()
        ).then(message -> System.out.println(message.body().encodePrettily()))
            .err(Throwable::printStackTrace);
    }

    private static void create(ModuleSystem module, MessageBus messageBus) {
        messageBus.sendAndReceive(
            MessageBus.Params.builder()
                .address(Addresses.add(Entities.USER_ENTITY))
                .message(
                    new JsonObject(
                        "{\n" +
                            "  \"userId\" : \"admin-5\",\n" +
                            "  \"username\" : \"fakmin5\",\n" +
                            "  \"email\" : \"fakmin@fakmin5\",\n" +
                            "  \"phone\" : \"01951883417\"\n" +
                            "}"
                    )
                )
                .userId("admin-1")
                .build()
        ).then(objectMessage -> {
            System.out.println(objectMessage.body());
            System.out.println(objectMessage.headers());
        });
    }

    private static void findOne(ModuleSystem module, MessageBus messageBus) {
        messageBus.sendAndReceiveJsonObject(
            MessageBus.Params.builder()
                .userId("admin-1")
                .address(Addresses.findOne(Entities.USER_ENTITY))
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

        module.require(MessageHandlersBuilder.class).build(
            MessageHandlersBuilder.BuildParams.builder()
                .module(module)
                .flowParamss(ImmutableList.of(
                    new MessageHandlersBuilder.FlowParams(
                        Entities.USER_ENTITY,
                        new FieldExpressionImpl(
                            "r.id"
                        )
                    )
                ))
                .build()
        ).forEach(addressAndHandler -> {
            eventBus.consumer(addressAndHandler.getAddress(), addressAndHandler.getMessageHandler());
        });

        return module;
    }
}
