package elasta.orm.json.insert_or_update;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 9/24/2016.
 */
public interface SqlInsertOrUpdate {
    Promise<List<UpdateSpec>> insertOrUpdate(String rootTable, JsonObject jsonObject);
}
