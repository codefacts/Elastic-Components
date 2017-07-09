package tracker.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.States;
import elasta.composer.model.request.OrderByModel;
import elasta.composer.model.request.QueryModel;
import elasta.composer.state.handlers.BeforeFindAllStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;
import tracker.model.PositionModel;
import tracker.model.UserModel;

/**
 * Created by sohan on 7/9/2017.
 */
final public class BeforeFindAllPositionsGroupByUserIdStateHandlerImpl implements BeforeFindAllStateHandler {

    @Override
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {
        System.out.println(States.beforeFindAll + "positions");
        return Promises.of(
            Flow.trigger(Events.next, msg.withBody(
                query(msg)
            ))
        );
    }

    private JsonObject query(Msg<JsonObject> msg) {

        String groupByKey = "r." + PositionModel.createdBy + "." + UserModel.userId;

        return new JsonObject(ImmutableMap.of(
            QueryModel.groupBy, ImmutableList.of(
                groupByKey
            ),
            QueryModel.paginationKey, groupByKey,
            QueryModel.orderBy, ImmutableList.of(
                ImmutableMap.of(
                    OrderByModel.field, "r." + PositionModel.createDate,
                    OrderByModel.order, "DESC"
                )
            )
        ));
    }
}
