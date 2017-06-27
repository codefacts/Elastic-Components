package elasta.orm.upsert.impl;

import elasta.orm.upsert.LongIdGenerator;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by sohan on 5/15/2017.
 */
final public class LongIdGeneratorImpl implements LongIdGenerator {

    @Override
    public Promise<Long> nextId(String entity) {
        Objects.requireNonNull(entity);

        return Promises.of(
            UUID.randomUUID().getLeastSignificantBits() >>> 1
        );
    }

    public static void main() {
    }
}
