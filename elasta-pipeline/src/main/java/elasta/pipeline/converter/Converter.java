package elasta.pipeline.converter;

/**
 * Created by Jango on 2016-11-20.
 */
public interface Converter<T, R> {
    R convert(T t);
}
