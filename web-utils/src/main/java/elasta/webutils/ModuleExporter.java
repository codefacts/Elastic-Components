package elasta.webutils;

import elasta.module.ModuleSystem;

/**
 * Created by Jango on 9/12/2016.
 */
public interface ModuleExporter {

    void exportModule(ModuleSystem moduleSystem);

    static ModuleExporter get() {
        return moduleSystem -> {

            moduleSystem.export(module -> {
                module.export(new WebUtilsImpl());
            }, WebUtils.class);

        };
    }
}
