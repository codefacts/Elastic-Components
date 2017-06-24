package elasta.vertxutils;

import elasta.module.MutableModuleSystem;

/**
 * Created by Jango on 9/12/2016.
 */
public interface VertxUtilsExporter {

    void export(MutableModuleSystem mutableModuleSystem);

    static VertxUtilsExporter get() {
        return moduleSystem -> {
        };
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
