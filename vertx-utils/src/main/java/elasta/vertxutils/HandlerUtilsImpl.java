package elasta.vertxutils;

import io.vertx.core.eventbus.Message;

/**
 * Created by Jango on 9/14/2016.
 */
public class HandlerUtilsImpl implements HandlerUtils {
    private final FailureCodeHandler defaultFailureCodeHandler;
    private final ReplyHandler defaultReplyHandler;

    public HandlerUtilsImpl(FailureCodeHandler defaultFailureCodeHandler, ReplyHandler defaultReplyHandler) {
        this.defaultFailureCodeHandler = defaultFailureCodeHandler;
        this.defaultReplyHandler = defaultReplyHandler;
    }

    @Override
    public <T, R> void handleMessage(Message<T> message, VertxMessageHandler<T, R> handler) {
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
}
