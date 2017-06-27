package elasta.orm.idgenerator.impl;

import elasta.core.promise.intfs.Promise;
import elasta.orm.idgenerator.StringObjectIdGenerator;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/28/2017.
 */
final public class StringObjectIdGeneratorImpl implements StringObjectIdGenerator {
    @Override
    public Promise<JsonObject> generateId(String entity, JsonObject jsonObject) {
        return null;
    }
}
