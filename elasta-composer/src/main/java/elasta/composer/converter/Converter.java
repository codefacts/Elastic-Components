package elasta.composer.converter;

/**
 * Created by sohan on 5/13/2017.
 */
public interface Converter<T, R> extends elasta.pipeline.converter.Converter<T, R> {
    R convert(T t);
}
