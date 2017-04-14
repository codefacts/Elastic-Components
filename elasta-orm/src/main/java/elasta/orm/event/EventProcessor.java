package elasta.orm.event;

import elasta.core.promise.intfs.Promise;
import elasta.orm.event.builder.EventProcessorBuilder;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 3/28/2017.
 */
public interface EventProcessor {

    Promise<JsonObject> processDelete(String entityName, JsonObject entity);

    Promise<JsonObject> processUpsert(String entityName, JsonObject entity);

    Promise<JsonObject> processDeleteRelation(String entityName, JsonObject entity);

    static void main(String[] asdf) {

    }
}
