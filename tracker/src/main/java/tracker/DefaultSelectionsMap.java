package tracker;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import tracker.entity_config.Entities;
import tracker.model.merchandiser.LocationModel;
import tracker.model.merchandiser.OutletImageModel;
import tracker.model.merchandiser.OutletModel;

import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 2017-07-25.
 */
public interface DefaultSelectionsMap {
    String rootAlias = "r";
    Map<EntityToDefaultSelectionsMap.EntityAndAction, EntityToDefaultSelectionsMap.SelectionsAndJoinParams> entityAndActionToSelectionsMap = ImmutableMap.of(
        new EntityToDefaultSelectionsMap.EntityAndAction(Entities.OUTLET_ENTITY, Addresses.findAll(Entities.OUTLET_ENTITY)),
        new EntityToDefaultSelectionsMap.SelectionsAndJoinParams(
            ImmutableList.of(
                new FieldExpressionImpl(rootAlias + "." + OutletModel.id),
                new FieldExpressionImpl(rootAlias + "." + OutletModel.name),
                new FieldExpressionImpl(rootAlias + "." + OutletModel.address),
                new FieldExpressionImpl(rootAlias + "." + OutletModel.qrCode),
                new FieldExpressionImpl(rootAlias + "." + OutletModel.location + "." + LocationModel.id),
                new FieldExpressionImpl(rootAlias + "." + OutletModel.location + "." + LocationModel.lat),
                new FieldExpressionImpl(rootAlias + "." + OutletModel.location + "." + LocationModel.lng),
                new FieldExpressionImpl("img" + "." + OutletImageModel.id),
                new FieldExpressionImpl("img" + "." + OutletImageModel.uri),
                new FieldExpressionImpl("img" + "." + OutletImageModel.title),
                new FieldExpressionImpl("img" + "." + OutletImageModel.description)
            ),
            ImmutableList.of(
                new QueryExecutor.JoinParam(PathExpression.create(rootAlias, OutletModel.images), "img")
            )
        )
    );

    static Map<EntityToDefaultSelectionsMap.EntityAndAction, EntityToDefaultSelectionsMap.SelectionsAndJoinParams> getMap() {
        return entityAndActionToSelectionsMap;
    }
}
