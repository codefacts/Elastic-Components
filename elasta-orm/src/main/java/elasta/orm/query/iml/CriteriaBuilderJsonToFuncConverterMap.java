package elasta.orm.query.iml;

import com.google.common.collect.ImmutableMap;
import elasta.criteria.json.mapping.JsonToFuncConverter;
import elasta.criteria.json.mapping.JsonToFuncConverterMap;
import elasta.criteria.json.mapping.Mp;
import elasta.orm.query.expression.builder.impl.QueryBuilderImpl;
import elasta.sql.SqlOps;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/21/2017.
 */
final public class CriteriaBuilderJsonToFuncConverterMap implements JsonToFuncConverterMap {
    static final String FIELD = SqlOps.field;
    final JsonToFuncConverterMap jsonToFuncConverterMap;
    final QueryBuilderImpl qb;

    public CriteriaBuilderJsonToFuncConverterMap(JsonToFuncConverterMap jsonToFuncConverterMap, QueryBuilderImpl qb) {
        Objects.requireNonNull(jsonToFuncConverterMap);
        Objects.requireNonNull(qb);
        this.jsonToFuncConverterMap = jsonToFuncConverterMap;
        this.qb = qb;
    }

    @Override
    public JsonToFuncConverter get(String operation) {
        Objects.requireNonNull(operation);
        if (Objects.equals(FIELD, operation)) {
            return fieldFuncBuilder();
        }
        return jsonToFuncConverterMap.get(operation);
    }

    private JsonToFuncConverter fieldFuncBuilder() {
        return (jsonObject, converterMap) -> qb.field(jsonObject.getString(Mp.arg));
    }

    @Override
    public Map<String, JsonToFuncConverter> getMap() {
        throw new UnsupportedOperationException();
    }
}
