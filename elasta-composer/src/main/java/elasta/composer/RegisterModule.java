package elasta.composer;

import elasta.module.ModuleSystem;

/**
 * Created by Jango on 9/12/2016.
 */
public class RegisterModule {
    public void register(ModuleSystem moduleSystem) {

        moduleSystem.export(App.class, module -> {
            module.export(new AppImpl());
        });

        moduleSystem.export(RegisterEventHandlers.class, module -> {
            module.export(new RegisterEventHandlersImpl());
        });
    }
}
