package elasta.vertxutils;

import elasta.module.ModuleSystem;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

/**
 * Created by Jango on 9/12/2016.
 */
public interface VertxUtilsExporter {

    void export(ModuleSystem moduleSystem);

    static VertxUtilsExporter get() {

        return moduleSystem -> {

            moduleSystem.export(FailureCodeHandler.class, module -> module.export(throwable -> new FailureTuple(500, "UNEXPECTED_ERROR")));

            moduleSystem.export(ReplyHandler.class, module -> module.export(Message::reply));

            moduleSystem.export(
                VertxUtils.class,
                module -> module.export(
                    new VertxUtilsImpl(
                        module.require(Vertx.class),
                        module.require(FailureCodeHandler.class), module.require(ReplyHandler.class)
                    )
                ));
        };
    }
}
