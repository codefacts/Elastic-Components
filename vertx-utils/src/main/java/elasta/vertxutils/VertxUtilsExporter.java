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
                SenderUtils.class,
                module -> module.export(
                    new SenderUtilsImpl(
                        module.require(Vertx.class)
                    )
                ));

            moduleSystem.export(HandlerUtils.class, module -> module.export(new HandlerUtilsImpl(module.require(FailureCodeHandler.class), module.require(ReplyHandler.class))));

            moduleSystem.export(DbUtils.class, module -> module.export(new DbUtilsImpl()));
        };
    }

    public static void main(String[] args) {
        System.out.println("ok");
    }
}
