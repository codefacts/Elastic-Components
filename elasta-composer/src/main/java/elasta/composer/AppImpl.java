package elasta.composer;

import elasta.composer.util.MessageBundle;
import elasta.composer.validator.ValidationPipeline;
import elasta.composer.validator.ValidationResult;
import elasta.composer.validator.composer.JsonObjectValidatorComposer;
import elasta.core.promise.impl.Promises;
import elasta.core.statemachine.StateMachine;
import elasta.core.statemachine.StateMachineBuilder;
import elasta.module.ModuleSystem;
import elasta.webutils.CrudBuilder;
import elasta.webutils.EventAddresses;
import elasta.webutils.ReqCnst;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;
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
        StateMachineBuilder machineBuilder = moduleSystem.require(StateMachineBuilder.class, EventAddresses.CREATE);

        JsonObjectValidatorComposer composer = new JsonObjectValidatorComposer(new ArrayList<>(), moduleSystem.require(MessageBundle.class));

        composer
            .field("name", fieldValidatorComposer -> fieldValidatorComposer.notNullEmptyOrWhiteSpace().stringType())
            .field("email", fieldValidatorComposer -> fieldValidatorComposer.stringType().email().notNullEmptyOrWhiteSpace())
            .field("phone", fieldValidatorComposer -> fieldValidatorComposer.stringType().phone().notNullEmptyOrWhiteSpace())
        ;

        ValidationPipeline<JsonObject> validationPipeline = new ValidationPipeline<>(composer.getValidatorList());

        machineBuilder.handlers(StateCnst.VALIDATE, (JsonObject val) -> {
            List<ValidationResult> validationResults = validationPipeline.validate(val.getJsonObject(ReqCnst.BODY));

            if (validationResults == null) {
                return Promises.just(StateMachine.triggerNext(val));
            } else {
                return Promises.just(StateMachine.trigger(EventCnst.VALIDATION_FAIL, validationResults));
            }
        });

        machineBuilder.handlers(StateCnst.CREATE, val -> {
            
        });

        return null;
    }
}
