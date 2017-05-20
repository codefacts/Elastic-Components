package elasta.composer.producer.impl;

import elasta.composer.producer.IdGenerator;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.Random;
import java.util.UUID;

/**
 * Created by sohan on 5/15/2017.
 */
final public class StringIdGeneratorImpl<T> implements IdGenerator<String> {

    @Override
    public Promise<String> nextId(String entity) {
        return Promises.of(
            UUID.randomUUID().toString()
        );
    }
}
