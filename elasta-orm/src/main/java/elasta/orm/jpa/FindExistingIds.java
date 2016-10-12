package elasta.orm.jpa;

import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 10/12/2016.
 */
public interface FindExistingIds {

    public Promise<TableIdPairs> findExistingTableIds(String model, JsonObject data);
}
