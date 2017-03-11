package elasta.orm.nm.delete.dependency;

import elasta.orm.nm.upsert.TableData;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by sohan on 3/12/2017.
 */
public interface ListTablesToDeleteFunction {
    void listTables(JsonObject entity, ListTablesToDeleteContext context);
}
