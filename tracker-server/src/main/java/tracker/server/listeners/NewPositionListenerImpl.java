package tracker.server.listeners;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.model.request.UserModel;
import elasta.orm.Orm;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.sql.JsonOps;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import tracker.entity_config.Entities;
import tracker.model.PositionModel;
import tracker.server.BrowserEvents;

import java.util.Objects;

/**
 * Created by sohan on 7/13/2017.
 */
final public class NewPositionListenerImpl implements NewPositionListener {
    final Orm orm;
    final EventBus eventBus;

    public NewPositionListenerImpl(Orm orm, EventBus eventBus) {
        Objects.requireNonNull(orm);
        Objects.requireNonNull(eventBus);
        this.orm = orm;
        this.eventBus = eventBus;
    }

    @Override
    public void listenTo(JsonObject position, Message msg) {
        orm
            .findOne(Entities.USER_ENTITY, "r",
                JsonOps.eq("r." + UserModel.userId, msg.headers().get(UserModel.userId)),
                ImmutableList.of(
                    new FieldExpressionImpl("r." + tracker.model.UserModel.id),
                    new FieldExpressionImpl("r." + tracker.model.UserModel.userId),
                    new FieldExpressionImpl("r." + tracker.model.UserModel.username),
                    new FieldExpressionImpl("r." + tracker.model.UserModel.firstName),
                    new FieldExpressionImpl("r." + tracker.model.UserModel.lastName)
                ))
            .map(user -> new JsonObject(
                ImmutableMap.<String, Object>builder()
                    .putAll(position.getMap())
                    .put(PositionModel.createdBy, user)
                    .build()
            ))
            .then(newPosition -> eventBus.publish(BrowserEvents.userPositionTracking, newPosition))
            .err(Throwable::printStackTrace)
        ;
    }
}
