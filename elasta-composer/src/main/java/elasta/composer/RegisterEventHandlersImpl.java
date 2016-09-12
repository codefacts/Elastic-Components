package elasta.composer;

import elasta.composer.event.handlers.*;
import elasta.module.ModuleSystem;
import elasta.vertxutils.VertxUtils;

/**
 * Created by Jango on 9/12/2016.
 */
public class RegisterEventHandlersImpl implements RegisterEventHandlers {
    @Override
    public void register(ModuleSystem moduleSystem) {

        moduleSystem.export(EventHandler.class, EventHandlers.CREATE, module -> {

            VertxUtils vertxUtils = module.require(VertxUtils.class);
            module.export(new CreateHandler(vertxUtils)::create);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.UPDATE_ALL_PROPERTIES, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            module.export(new UpdateAllPropertiesHandler(vertxUtils)::updateAllProperties);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.UPDATE_SOME_PROPERTIES, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            module.export(new UpdateSomePropertiesHandler(vertxUtils)::updateSomeProperties);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.DELETE, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            module.export(new DeleteHandler(vertxUtils)::delete);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.FIND_ALL, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            module.export(new FindAllHandler(vertxUtils)::findAll);
        });

        moduleSystem.export(EventHandler.class, EventHandlers.FIND, module -> {
            VertxUtils vertxUtils = module.require(VertxUtils.class);
            module.export(new FindHandler(vertxUtils)::find);
        });
    }
}
