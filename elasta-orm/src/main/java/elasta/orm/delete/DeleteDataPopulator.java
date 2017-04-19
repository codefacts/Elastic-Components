package elasta.orm.delete;

import elasta.orm.delete.impl.DeleteData;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 17/02/07.
 */
public interface DeleteDataPopulator {
    DeleteData populate(JsonObject jsonObject);
}
