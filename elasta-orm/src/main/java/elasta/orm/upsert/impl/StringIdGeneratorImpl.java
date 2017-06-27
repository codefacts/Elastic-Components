package elasta.orm.upsert.impl;

import elasta.orm.upsert.StringIdGenerator;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.UUID;

/**
 * Created by sohan on 5/15/2017.
 */
final public class StringIdGeneratorImpl implements StringIdGenerator {
    
    @Override
    public Promise<String> nextId(String entity) {
        return Promises.of(
            UUID.randomUUID().toString()
        );
    }
}
