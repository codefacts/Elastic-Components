package elasta.webutils;

import elasta.module.ModuleSystem;

/**
 * Created by Jango on 9/12/2016.
 */
public interface WebUtilsExporter {

    void exportModule(ModuleSystem moduleSystem);

    static WebUtilsExporter get() {
        return moduleSystem -> {

            moduleSystem.export(WebUtils.class, module -> {
                module.export(new WebUtilsImpl());
            });
        };
    }
}
