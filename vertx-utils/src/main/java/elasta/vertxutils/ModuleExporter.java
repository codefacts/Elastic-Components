package elasta.vertxutils;

import elasta.module.ModuleSystem;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

/**
 * Created by Jango on 9/12/2016.
 */
public interface ModuleExporter {

    void export(ModuleSystem moduleSystem);

    static ModuleExporter get() {

        return moduleSystem -> {

            moduleSystem.export(module -> module.export(throwable -> new FailureTuple(500, "UNEXPECTED_ERROR")), FailureCodeHandler.class);

            moduleSystem.export(module -> module.export(Message::reply), ReplyHandler.class);

            moduleSystem.export(
                module -> module.export(
                    new VertxUtilsImpl(
                        module.require(Vertx.class),
                        module.require(FailureCodeHandler.class), module.require(ReplyHandler.class)
                    )
                ), VertxUtils.class);
        };
    }
}
