package elasta.composer;

import elasta.composer.event.machines.*;
import elasta.module.ModuleSystem;
import elasta.vertxutils.VertxUtils;
import elasta.webutils.EventHandler;
import elasta.webutils.EventAddresses;
import elasta.webutils.StateMachineStarter;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 9/12/2016.
 */
public class RegisterEventHandlersImpl implements RegisterEventHandlers {
    @Override
    public void register(ModuleSystem moduleSystem) {

        moduleSystem.export(EventHandler.class, EventAddresses.CREATE, module -> {

            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> create = new CreateMachineProfile(vertxUtils, module.require(StateMachineStarter.class), machine)::create;
            module.export(create);
        });

        moduleSystem.export(EventHandler.class, EventAddresses.UPDATE_ALL_PROPERTIES, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> updateAllProperties = new UpdateAllPropertiesMachine(vertxUtils, module.require(StateMachineStarter.class))::updateAllProperties;
            module.export(updateAllProperties);
        });

        moduleSystem.export(EventHandler.class, EventAddresses.UPDATE_SOME_PROPERTIES, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> updateSomeProperties = new UpdateSomePropertiesMachine(vertxUtils, module.require(StateMachineStarter.class))::updateSomeProperties;
            module.export(updateSomeProperties);
        });

        moduleSystem.export(EventHandler.class, EventAddresses.DELETE, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> delete = new DeleteMachine(vertxUtils, module.require(StateMachineStarter.class))::delete;
            module.export(delete);
        });

        moduleSystem.export(EventHandler.class, EventAddresses.FIND_ALL, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> findAll = new FindAllMachine(vertxUtils, module.require(StateMachineStarter.class))::findAll;
            module.export(findAll);
        });

        moduleSystem.export(EventHandler.class, EventAddresses.FIND, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            EventHandler<JsonObject> find = new FindMachine(vertxUtils, module.require(StateMachineStarter.class))::find;
            module.export(find);
        });
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
