package elasta.orm.relation.delete;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 4/8/2017.
 */
@FunctionalInterface
public interface RelationFieldHandler {

    void handleDeleteRelation(JsonObject parent, DeleteChildRelationsContext context);
}