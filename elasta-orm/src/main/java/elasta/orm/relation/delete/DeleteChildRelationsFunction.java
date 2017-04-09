package elasta.orm.relation.delete;

import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 4/7/2017.
 */
public interface DeleteChildRelationsFunction {
    void deleteChildRelations(JsonObject entity, DeleteChildRelationsContext context);
}
