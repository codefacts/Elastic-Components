package elasta.orm.nm.delete;

import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-25.
 */
public class DeleteFunctionImpl implements DeleteFunction {
    
    @Override
    public TableData delete(JsonObject entity, DeleteContext context) {

        return null;
    }
}
