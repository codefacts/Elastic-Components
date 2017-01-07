package elasta.orm.nm.query.json.mapping;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface JsonToFuncConverterRegistry {
    JsonToFuncConverter get(String operation);
}
