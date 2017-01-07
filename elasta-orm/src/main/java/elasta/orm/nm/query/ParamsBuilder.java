package elasta.orm.nm.query;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface ParamsBuilder {
    String add(Object value);
}
