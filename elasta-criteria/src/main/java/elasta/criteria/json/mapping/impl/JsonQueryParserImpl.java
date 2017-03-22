package elasta.criteria.json.mapping.impl;

import elasta.criteria.ParamsBuilder;
import elasta.criteria.json.mapping.JsonQueryParser;
import elasta.criteria.json.mapping.JsonToFuncConverter;
import elasta.criteria.json.mapping.JsonToFuncConverterMap;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
final public class JsonQueryParserImpl implements JsonQueryParser {
    final JsonToFuncConverterMap jsonToFuncConverterMap;
    final JsonToFuncConverter jsonToFuncConverter;

    public JsonQueryParserImpl(JsonToFuncConverterMap jsonToFuncConverterMap, JsonToFuncConverter jsonToFuncConverter) {
        Objects.requireNonNull(jsonToFuncConverterMap);
        Objects.requireNonNull(jsonToFuncConverter);
        this.jsonToFuncConverterMap = jsonToFuncConverterMap;
        this.jsonToFuncConverter = jsonToFuncConverter;
    }

    @Override
    public String toSql(JsonObject query, ParamsBuilder paramsBuilder) {

        return jsonToFuncConverter.convert(query, jsonToFuncConverterMap).get(paramsBuilder);
    }
}
