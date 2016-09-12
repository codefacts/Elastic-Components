package elasta.module;

/**
 * Created by Jango on 9/12/2016.
 */
@FunctionalInterface
public interface ExportScript<T> {

    void run(Module<T> module);
}
