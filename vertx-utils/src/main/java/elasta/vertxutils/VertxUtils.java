package elasta.vertxutils;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 9/11/2016.
 */
final public class VertxUtils {
    private final Vertx vertx;
    private final FailureCodeHandler defaultFailureCodeHandler;
    private final ReplyHandler defaultReplyHandler;

    public VertxUtils(Vertx vertx, FailureCodeHandler defaultFailureCodeHandler, ReplyHandler defaultReplyHandler) {
        this.vertx = vertx;
        this.defaultFailureCodeHandler = defaultFailureCodeHandler;
        this.defaultReplyHandler = defaultReplyHandler;
    }

    public <T> Promise<T> send(String address, Object message) {

        Defer<T> defer = Promises.defer();

        vertx.eventBus().send(address, message, (AsyncResult<Message<T>> event) -> {
            if (event.failed()) {
                defer.reject(event.cause());
            } else {
                defer.resolve(event.result().body());
            }
        });

        return defer.promise();
    }

    public <T> void handleMessage(Message<T> message, VertxMessageHandler handler) {
        try {
            handler.handle(message.body(), message.headers(), message.address(), message.replyAddress())
                .cmp(signal -> {
                    if (signal.isError()) {
                        FailureTuple failureTuple = defaultFailureCodeHandler.handleFailure(signal.err());
                        message.fail(failureTuple.getCode(), failureTuple.getMessageCode());
                    } else {
                        defaultReplyHandler.handleReply(message, signal.val());
                    }
                })
            ;
        } catch (Throwable throwable) {
            FailureTuple failureTuple = defaultFailureCodeHandler.handleFailure(throwable);
            message.fail(failureTuple.getCode(), failureTuple.getMessageCode());
        }
    }

    public Promise<JsonObject> sendAndReceiveJsonObject(String address, Object jsonReq) {
        return send(address, jsonReq);
    }

    public Promise<JsonArray> sendAndReceiveJsonArray(String address, Object jsonReq) {
        return send(address, jsonReq);
    }
}
