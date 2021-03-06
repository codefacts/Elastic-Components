package tracker.message.handlers.impl;

import com.google.common.base.Preconditions;
import elasta.composer.AppDateTimeFormatter;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.orm.BaseOrm;
import io.vertx.core.AsyncResult;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;
import tracker.TrackerUtils;
import tracker.message.handlers.ReplayMessageHandler;
import tracker.model.request.ReplayModel;
import tracker.services.impl.ReplayServiceImpl;

import java.util.Objects;

/**
 * Created by sohan on 2017-07-26.
 */
final public class ReplayMessageHandlerImpl implements ReplayMessageHandler {
    final BaseOrm orm;
    final AppDateTimeFormatter appDateTimeFormatter;
    final MessageProcessingErrorHandler messageProcessingErrorHandler;

    public ReplayMessageHandlerImpl(BaseOrm orm, AppDateTimeFormatter appDateTimeFormatter, MessageProcessingErrorHandler messageProcessingErrorHandler) {
        Objects.requireNonNull(orm);
        Objects.requireNonNull(appDateTimeFormatter);
        Objects.requireNonNull(messageProcessingErrorHandler);
        this.orm = orm;
        this.appDateTimeFormatter = appDateTimeFormatter;
        this.messageProcessingErrorHandler = messageProcessingErrorHandler;
    }

    @Override
    public void handle(Message<JsonObject> message) {
        final JsonObject request = message.body();
        Objects.requireNonNull(request);
        final long startTime = request.getLong(ReplayModel.startTime);
        final Long endTime = request.getLong(ReplayModel.endTime);
        final int totalSlots = request.getInteger(ReplayModel.totalSlots);

        final int reqSlots = Math.min(request.getInteger(ReplayModel.requestedSlots), totalSlots);
        final long step = (endTime - startTime) / totalSlots;

        System.out.println("### RequestReceived");

        reqReplyLoop(
            LoopContext.builder()
                .message(message)
                .startTime(startTime)
                .endTime(endTime)
                .totalSlots(totalSlots)
                .step(step)
                .reqSlots(reqSlots)
                .slotsReturned(0)
                .build()
        );
    }

    private void reqReplyLoop(LoopContext context) {

        final int reqSlots = Math.min(context.getReqSlots(), context.getTotalSlots() - context.getSlotsReturned());
        final Message<JsonObject> message = context.getMessage();

        if (reqSlots <= 0) {
            message.reply(TrackerUtils.emptyJsonArray());
            return;
        }

        new ReplayServiceImpl(
            orm, appDateTimeFormatter, context.getStartTime(), context.getStep()
        )
            .next(reqSlots)
            .toList()
            .doOnSuccess(jsonObjects -> {

                System.out.println("#### size: " + jsonObjects.size());

                if ((reqSlots + context.getSlotsReturned()) >= context.getTotalSlots()) {

                    System.out.println("[[[ #### returning final reply #### ]]]");
                    message.reply(new JsonArray(jsonObjects));
                    return;
                }

                message.reply(
                    new JsonArray(jsonObjects),
                    (AsyncResult<Message<JsonObject>> event) -> {

                        if (event.failed()) {
                            messageProcessingErrorHandler.handleError(event.cause(), message);
                            return;
                        }

                        try {

                            final Message<JsonObject> replyMessage = event.result();
                            final JsonObject req = replyMessage.body();
                            Objects.requireNonNull(req);

                            reqReplyLoop(
                                LoopContext.builder()
                                    .endTime(context.getEndTime())
                                    .step(context.getStep())
                                    .totalSlots(context.getTotalSlots())
                                    .message(replyMessage)
                                    .startTime(context.getStartTime() + context.getStep() * reqSlots)
                                    .slotsReturned(context.getSlotsReturned() + reqSlots)
                                    .reqSlots(req.getInteger(ReplayModel.requestedSlots, 0))
                                    .build()
                            );
                        } catch (Exception ex) {
                            messageProcessingErrorHandler.handleError(ex, event.result());
                        }
                    }
                );
            })
            .subscribe(TrackerUtils.noopsDoOnNext(), throwable -> {
                System.out.println("#### Error in reply.....");
                messageProcessingErrorHandler.handleError(throwable, message);
            })
        ;

    }

    @Value
    @Builder
    static final class LoopContext {
        final long startTime;
        final Long endTime;
        final int totalSlots;
        final int reqSlots;
        final long step;
        final int slotsReturned;
        final Message<JsonObject> message;

        public LoopContext(long startTime, Long endTime, int totalSlots, int reqSlots, long step, int slotsReturned, Message<JsonObject> message) {
            Preconditions.checkArgument(startTime > 0);
            Preconditions.checkArgument(endTime > 0);
            Preconditions.checkArgument(totalSlots > 0);
            Preconditions.checkArgument(step > 0);
            this.startTime = startTime;
            this.endTime = endTime;
            this.totalSlots = totalSlots;
            this.reqSlots = reqSlots;
            this.step = step;
            this.slotsReturned = slotsReturned;
            Objects.requireNonNull(message);
            this.message = message;
        }
    }
}
