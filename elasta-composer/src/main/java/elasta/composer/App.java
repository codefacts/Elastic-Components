package elasta.composer;

import elasta.vertxutils.FailureTuple;
import elasta.vertxutils.VertxUtils;
import elasta.webutils.WebUtils;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;

/**
 * Created by Shahadat on 8/31/2016.
 */
public interface App {

    WebUtils webUtils();

    VertxUtils vertxUtils();

    static App app(Vertx vertx) {
        return new AppImpl(new WebUtils(), new VertxUtils(vertx,
            throwable -> {
                return new FailureTuple(FailureCodes.UNEXPECTED_ERROR.code(), throwable.toString());
            },
            (message, message2) -> {
                message.reply(message2);
            }));
    }
}
