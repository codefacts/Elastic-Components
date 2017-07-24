package elasta.orm.idgenerator.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.idgenerator.LongIdGenerator;

/**
 * Created by sohan on 2017-07-24.
 */
final public class TimestampBasedLongIdGenerator implements LongIdGenerator {
    @Override
    public Promise<Long> nextId(String entity) {
        return Promises.of(
            System.nanoTime()
        );
    }
}
