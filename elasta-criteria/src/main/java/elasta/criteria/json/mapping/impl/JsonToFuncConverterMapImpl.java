package elasta.criteria.json.mapping.impl;

import elasta.criteria.json.mapping.JsonToFuncConverter;
import elasta.criteria.json.mapping.ex.JsonToFuncConverterException;
import elasta.criteria.json.mapping.JsonToFuncConverterMap;

import java.util.Map;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
final public class JsonToFuncConverterMapImpl implements JsonToFuncConverterMap {
    final Map<String, JsonToFuncConverter> converterMap;

    public JsonToFuncConverterMapImpl(Map<String, JsonToFuncConverter> converterMap) {
        Objects.requireNonNull(converterMap);
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
