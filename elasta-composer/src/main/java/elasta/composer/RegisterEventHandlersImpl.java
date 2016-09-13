package elasta.composer;

import elasta.composer.event.handlers.*;
import elasta.module.ModuleSystem;
import elasta.vertxutils.VertxUtils;
import elasta.webutils.EventHandler;
import elasta.webutils.EventHandlers;
import elasta.webutils.StateMachineStarter;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 9/12/2016.
 */
public class RegisterEventHandlersImpl implements RegisterEventHandlers {
    @Override
    public void register(ModuleSystem moduleSystem) {

        moduleSystem.export(EventHandler.class, EventHandlers.CREATE, module -> {

            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> create = new CreateHandler(vertxUtils, module.require(StateMachineStarter.class))::create;
            module.export(create);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.UPDATE_ALL_PROPERTIES, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> updateAllProperties = new UpdateAllPropertiesHandler(vertxUtils, module.require(StateMachineStarter.class))::updateAllProperties;
            module.export(updateAllProperties);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.UPDATE_SOME_PROPERTIES, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> updateSomeProperties = new UpdateSomePropertiesHandler(vertxUtils, module.require(StateMachineStarter.class))::updateSomeProperties;
            module.export(updateSomeProperties);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.DELETE, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> delete = new DeleteHandler(vertxUtils, module.require(StateMachineStarter.class))::delete;
            module.export(delete);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.FIND_ALL, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> findAll = new FindAllHandler(vertxUtils, module.require(StateMachineStarter.class))::findAll;
            module.export(findAll);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.FIND, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> find = new FindHandler(vertxUtils, module.require(StateMachineStarter.class))::find;
            module.export(find);
        });
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
