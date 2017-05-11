package elasta.webutils.query.string;

/**
 * Created by Jango on 2016-11-20.
 */
public interface ObjectToQueryStringConverter<T> {
    String convert(T t);
}
