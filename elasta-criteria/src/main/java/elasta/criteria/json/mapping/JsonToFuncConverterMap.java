package elasta.criteria.json.mapping;

/**
 * Created by Jango on 2017-01-07.
 */
@FunctionalInterface
public interface JsonToFuncConverterMap {
    JsonToFuncConverter get(String operation);
}
