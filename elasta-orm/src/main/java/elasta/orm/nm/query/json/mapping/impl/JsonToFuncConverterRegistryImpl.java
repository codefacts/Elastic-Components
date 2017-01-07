package elasta.orm.nm.query.json.mapping.impl;

import elasta.orm.nm.query.json.mapping.JsonToFuncConverter;
import elasta.orm.nm.query.json.mapping.JsonToFuncConverterRegistry;
import elasta.orm.nm.query.json.mapping.ex.JsonToFuncConverterException;

import java.util.Map;

/**
 * Created by Jango on 2017-01-07.
 */
public class JsonToFuncConverterRegistryImpl implements JsonToFuncConverterRegistry {
    final Map<String, JsonToFuncConverter> converterMap;

    public JsonToFuncConverterRegistryImpl(Map<String, JsonToFuncConverter> converterMap) {
        this.converterMap = converterMap;
    }

    @Override
    public JsonToFuncConverter get(String operation) {
        JsonToFuncConverter converter = converterMap.get(operation);
        if (converter == null) {
            throw new JsonToFuncConverterException("No converter found for operation '" + operation + "'");
        }
        return converter;
    }
}
