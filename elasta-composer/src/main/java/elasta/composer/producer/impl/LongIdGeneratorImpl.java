package elasta.composer.producer.impl;

import elasta.composer.producer.IdGenerator;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by sohan on 5/15/2017.
 */
final public class LongIdGeneratorImpl<T> implements IdGenerator<Long> {

    @Override
    public Promise<Long> nextId(String entity) {
        Objects.requireNonNull(entity);
        return Promises.of(
            ThreadLocalRandom.current().nextLong()
        );
    }
}
