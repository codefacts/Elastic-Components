package elasta.module.impl;

import elasta.module.ExportScript;

/**
 * Created by Jango on 9/12/2016.
 */
final public class ModuleInfo<T> {
    final ExportScript<T> exportScript;
    final boolean isPrototype;

    public ModuleInfo(ExportScript<T> exportScript, boolean isPrototype) {
        this.exportScript = exportScript;
        this.isPrototype = isPrototype;
    }
}
