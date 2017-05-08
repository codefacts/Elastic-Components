package elasta.criteria.json.mapping;

import java.util.Map;

/**
 * Created by Jango on 2017-01-07.
 */
public interface JsonToFuncConverterMap {

    JsonToFuncConverter get(String operation);

    Map<String, JsonToFuncConverter> getMap();
}
