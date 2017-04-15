package elasta.orm.table.data.populator;

import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Optional;

/**
 * Created by sohan on 4/15/2017.
 */
public interface DirectDependencyDataPopulator {

    String field();

    Map<String, Object> populateColumnToValueMap(Optional<JsonObject> childOptional);
}
