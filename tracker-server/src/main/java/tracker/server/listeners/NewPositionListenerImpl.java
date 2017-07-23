package tracker.server.listeners;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.MessageProcessingErrorHandler;
import elasta.composer.model.request.UserModel;
import elasta.orm.Orm;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.JsonOps;
import io.reactivex.subjects.PublishSubject;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;
import tracker.TrackerUtils;
import tracker.entity_config.Entities;
import tracker.model.PositionModel;
import tracker.server.BrowserEvents;
import tracker.server.ErrorHandler;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created by sohan on 7/13/2017.
 */
final public class NewPositionListenerImpl implements NewPositionListener {
    final Orm orm;
    final EventBus eventBus;
    final MessageProcessingErrorHandler messageProcessingErrorHandler;
    final ErrorHandler errorHandler;
    final PublishSubject<PositionAndUserId> publishSubject = PublishSubject.create();

    public NewPositionListenerImpl(Orm orm, EventBus eventBus, MessageProcessingErrorHandler messageProcessingErrorHandler, ErrorHandler errorHandler) {
        Objects.requireNonNull(orm);
        Objects.requireNonNull(eventBus);
        Objects.requireNonNull(messageProcessingErrorHandler);
        Objects.requireNonNull(errorHandler);
        this.orm = orm;
        this.eventBus = eventBus;
        this.messageProcessingErrorHandler = messageProcessingErrorHandler;
        this.errorHandler = errorHandler;
        compose();
    }

    private void compose() {

        publishSubject
            .doOnNext(positionAndUserId -> {

                orm
                    .findOne(Entities.USER_ENTITY, "r",
                        JsonOps.eq("r." + UserModel.userId, positionAndUserId.userId),
                        ImmutableList.of(
                            new FieldExpressionImpl("r." + tracker.model.UserModel.id),
                            new FieldExpressionImpl("r." + tracker.model.UserModel.userId),
                            new FieldExpressionImpl("r." + tracker.model.UserModel.username),
                            new FieldExpressionImpl("r." + tracker.model.UserModel.firstName),
                            new FieldExpressionImpl("r." + tracker.model.UserModel.lastName)
                        ))
                    .map(user -> new JsonObject(
                        ImmutableMap.<String, Object>builder()
                            .putAll(positionAndUserId.getPosition().getMap())
                            .put(PositionModel.createdBy, user)
                            .build()
                    ))
                    .then(newPosition -> eventBus.publish(BrowserEvents.userPositionTracking, newPosition))
                    .err(throwable -> messageProcessingErrorHandler.handleError(throwable, positionAndUserId.getMessage()))
                ;

            })
            .doOnError(errorHandler::handleError)
            .retry()
            .subscribe(TrackerUtils.noopsDoOnNext(), errorHandler::handleError)
        ;
    }

    @Override
    public void listenTo(JsonObject position, Message msg) {

        publishSubject.onNext(new PositionAndUserId(position, msg.headers().get(UserModel.userId), msg));
    }

    @Value
    @Builder
    static final class PositionAndUserId {
        final JsonObject position;
        final String userId;
        final Message message;

        PositionAndUserId(JsonObject position, String userId, Message message) {
            Objects.requireNonNull(position);
            Objects.requireNonNull(userId);
            Objects.requireNonNull(message);
            this.position = position;
            this.userId = userId;
            this.message = message;
        }
    }
}
