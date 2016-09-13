package elasta.composer;

import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationPipeline;
import elasta.composer.validator.composer.JsonObjectValidatorComposer;
import elasta.core.statemachine.StateMachine;
import elasta.core.statemachine.StateMachineBuilder;
import elasta.module.ModuleSystem;
import elasta.webutils.CrudBuilder;
import elasta.webutils.EventAddresses;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Jango on 9/11/2016.
 */
public class AppImpl {

    public static void main(String[] args) {

        ModuleSystem moduleSystem = ModuleSystem.create();

        new RegisterModules().register(moduleSystem);

        Router router = Router.router(moduleSystem.require(Vertx.class));

        CrudBuilder crudBuilder = moduleSystem.require(CrudBuilder.class);

        crudBuilder.addRoutesAndHandlers(router, "/api", "users", machineMapUsers(moduleSystem));

        crudBuilder.addRoutesAndHandlers(router, "/api", "books");

        crudBuilder.addRoutesAndHandlers(router, "/api", "taxes");

        crudBuilder.addRoutesAndHandlers(router, "/api", "tokens");

        moduleSystem.require(Vertx.class).createHttpServer().requestHandler(router::accept).listen(6500);

        System.out.println("started");
    }

    private static Map<String, StateMachine> machineMapUsers(ModuleSystem moduleSystem) {
        StateMachineBuilder builder = moduleSystem.require(StateMachineBuilder.class, EventAddresses.CREATE);

        JsonObjectValidatorComposer composer = new JsonObjectValidatorComposer(new ArrayList<>(), moduleSystem.require(MessageBundle.class));



        new ValidationPipeline<>(composer.getValidatorList());

        return null;
    }
}
