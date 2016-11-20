package elasta.composer.pipeline.converter;

import elasta.core.promise.intfs.Promise;

/**
 * Created by Jango on 2016-11-20.
 */
public interface ConverterAsync<T, R> extends Converter<T, Promise<R>> {
}
