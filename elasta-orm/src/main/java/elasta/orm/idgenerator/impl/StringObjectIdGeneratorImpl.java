package elasta.orm.idgenerator.impl;

import elasta.core.promise.intfs.Promise;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.orm.idgenerator.StringIdGenerator;
import elasta.orm.idgenerator.StringObjectIdGenerator;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/28/2017.
 */
final public class StringObjectIdGeneratorImpl implements StringObjectIdGenerator {
    final ObjectIdGenerator<String> stringObjectIdGenerator;

    public StringObjectIdGeneratorImpl(EntityMappingHelper helper, StringIdGenerator stringIdGenerator, String isNewKey) {
        this.stringObjectIdGenerator = new ObjectIdGeneratorImpl<>(
            helper, stringIdGenerator, isNewKey
        );
    }

    @Override
    public Promise<JsonObject> generateId(String entity, JsonObject jsonObject) {
        return stringObjectIdGenerator.generateId(entity, jsonObject);
    }
}
