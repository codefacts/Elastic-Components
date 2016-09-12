package elasta.module;

/**
 * Created by Jango on 9/12/2016.
 */
public class Main {
    public static void main(String[] args) {
        ModuleSystem moduleSystem = ModuleSystem.create();

        moduleSystem.export(module -> {
            module.export("newModule1");
        }, String.class, "prop1");

        moduleSystem.export(module -> {
            String prop1 = module.require(String.class, "prop1");
            module.export(prop1 + " newModule2");
        }, String.class, "prop2");

        String require = moduleSystem.require(String.class);
        System.out.println(require);

        System.out.println(moduleSystem.require(String.class, "prop1"));
        System.out.println(moduleSystem.require(String.class, "prop2"));
    }
}
