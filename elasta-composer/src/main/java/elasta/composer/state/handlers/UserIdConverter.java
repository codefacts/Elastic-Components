package elasta.composer.state.handlers;

import elasta.pipeline.converter.Converter;

/**
 * Created by sohan on 5/12/2017.
 */
public interface UserIdConverter<R> extends Converter<Object, R> {
    @Override
    R convert(Object o);
}
