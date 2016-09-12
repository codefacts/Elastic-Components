package elasta.module.impl;

/**
 * Created by Jango on 9/12/2016.
 */
public class ModuleSpec {
    final Class moduleClass;
    final String moduleName;

    public ModuleSpec(Class moduleClass) {
        this(moduleClass, null);
    }

    public ModuleSpec(Class moduleClass, String moduleName) {
        this.moduleClass = moduleClass;
        this.moduleName = moduleName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModuleSpec that = (ModuleSpec) o;

        if (moduleClass != null ? !moduleClass.equals(that.moduleClass) : that.moduleClass != null) return false;
        return moduleName != null ? moduleName.equals(that.moduleName) : that.moduleName == null;

    }

    @Override
    public int hashCode() {
        int result = moduleClass != null ? moduleClass.hashCode() : 0;
        result = 31 * result + (moduleName != null ? moduleName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ModuleSpec{" +
            "moduleClass=" + moduleClass +
            ", moduleName='" + moduleName + '\'' +
            '}';
    }
}
