package elasta.orm.table.data.populator;

import elasta.orm.table.data.populator.DirectDependencyDataPopulator;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.Optional;

/**
 * Created by sohan on 4/15/2017.
 */
final public class DirectDependencyDataPopulatorImpl implements DirectDependencyDataPopulator {
//    final String field;


    @Override
    public String field() {
        return null;
    }

    @Override
    public Map<String, Object> populateColumnToValueMap(Optional<JsonObject> childOptional) {
        return null;
    }
}
