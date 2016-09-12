package elasta.module;

/**
 * Created by Jango on 9/12/2016.
 */
public class Main {
    public static void main(String[] args) {
        ModuleSystem moduleSystem = ModuleSystem.create();

        moduleSystem.exportPrototype(String.class, "prop1", module -> {
            module.export("newModule1");
            System.out.println("11");
        });

        moduleSystem.export(String.class, "prop2", module -> {
            String prop1 = module.require(String.class, "prop1");
            module.export(prop1 + " newModule2");
            System.out.println("11");
        });

        String require = moduleSystem.require(String.class);
        System.out.println(require);

        System.out.println(moduleSystem.require(String.class, "prop1"));
        System.out.println(moduleSystem.require(String.class, "prop2"));
    }
}
