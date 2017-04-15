package elasta.orm.table.data.populator;

import elasta.orm.upsert.TableData;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 4/15/2017.
 */
public interface TableDataPopulator {
    TableData populate(JsonObject jsonObject);
}
