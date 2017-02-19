package elasta.orm.nm.criteria.json.mapping.impl;

import elasta.orm.nm.criteria.ParamsBuilder;
import elasta.orm.nm.criteria.json.mapping.JsonQueryParser;
import elasta.orm.nm.criteria.json.mapping.JsonToFuncConverter;
import elasta.orm.nm.criteria.json.mapping.JsonToFuncConverterRegistry;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 2017-01-07.
 */
public class JsonQueryParserImpl implements JsonQueryParser {
    final JsonToFuncConverterRegistry registry;

    public JsonQueryParserImpl(JsonToFuncConverterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String toSql(JsonObject query, ParamsBuilder paramsBuilder) {

        String op = query.getString("op");

        JsonToFuncConverter converter = registry.get(op);

        return converter.convert(query, registry).get(paramsBuilder);
    }
}