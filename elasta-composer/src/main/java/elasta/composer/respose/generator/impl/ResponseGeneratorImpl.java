package elasta.composer.respose.generator.impl;

import elasta.composer.respose.generator.ResponseGenerator;

/**
 * Created by sohan on 6/30/2017.
 */
final public class ResponseGeneratorImpl<T, R> implements ResponseGenerator<T, R> {
    @Override
    public R apply(T t) throws Throwable {
        return ((R) t);
    }
}
