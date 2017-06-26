package elasta.criteria.json.mapping.impl;

import elasta.criteria.Func;
import elasta.criteria.ParamsBuilder;
import elasta.criteria.json.mapping.GenericJsonToFuncConverter;
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
    final GenericJsonToFuncConverter genericJsonToFuncConverter;

    public JsonQueryParserImpl(JsonToFuncConverterMap jsonToFuncConverterMap, GenericJsonToFuncConverter genericJsonToFuncConverter) {
        Objects.requireNonNull(jsonToFuncConverterMap);
        Objects.requireNonNull(genericJsonToFuncConverter);
        this.jsonToFuncConverterMap = jsonToFuncConverterMap;
        this.genericJsonToFuncConverter = genericJsonToFuncConverter;
    }

    @Override
    public String toSql(JsonObject query, ParamsBuilder paramsBuilder) {

        return genericJsonToFuncConverter.convert(query, jsonToFuncConverterMap).get(paramsBuilder);
    }
}
