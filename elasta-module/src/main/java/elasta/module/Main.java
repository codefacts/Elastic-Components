package elasta.module;

/**
 * Created by Jango on 9/12/2016.
 */
public class Main {
    public static void main(String[] args) {
        ModuleSystem moduleSystem = ModuleSystem.builder()
            .export(String.class, "1", module -> module.export("module 1"))
            .export(String.class, module -> module.export("ff"))
            .export(Integer.class, module -> module.export(1))
            .export(Boolean.class, module -> {
                System.out.println(module.require(Integer.class));
                System.out.println(module.require(String.class));
                System.out.println(module.require(String.class, "1"));
                module.export(true);
            })
            .build();
        Boolean require = moduleSystem.require(Boolean.class);
        System.out.println(require);

    }
}
