package elasta.orm.idgenerator.impl;

import elasta.core.promise.intfs.Promise;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.idgenerator.IdGenerator;
import elasta.orm.idgenerator.LongIdGenerator;
import elasta.orm.idgenerator.LongObjectIdGenerator;
import elasta.orm.idgenerator.ObjectIdGenerator;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 6/28/2017.
 */
final public class LongObjectIdGeneratorImpl implements LongObjectIdGenerator {
    final ObjectIdGenerator<Long> longObjectIdGenerator;

    public LongObjectIdGeneratorImpl(EntityMappingHelper helper, LongIdGenerator longIdGenerator, String isNewKey) {
        this.longObjectIdGenerator = new ObjectIdGeneratorImpl<>(
            helper, longIdGenerator, isNewKey
        );
    }

    @Override
    public Promise<JsonObject> generateId(String entity, JsonObject jsonObject) {
        return longObjectIdGenerator.generateId(entity, jsonObject);
    }
}
