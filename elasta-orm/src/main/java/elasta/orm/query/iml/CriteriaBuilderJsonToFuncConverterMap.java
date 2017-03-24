package elasta.orm.query.iml;

import elasta.criteria.json.mapping.JsonToFuncConverter;
import elasta.criteria.json.mapping.JsonToFuncConverterMap;
import elasta.criteria.json.mapping.Mp;
import elasta.orm.query.expression.builder.impl.QueryBuilderImpl;

import java.util.Objects;

/**
 * Created by sohan on 3/21/2017.
 */
final public class CriteriaBuilderJsonToFuncConverterMap implements JsonToFuncConverterMap {
    static final String FIELD = "field";
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
        if (FIELD.equals(operation)) {
            return (jsonObject, converterMap) -> qb.field(jsonObject.getString(Mp.arg1));
        }
        return jsonToFuncConverterMap.get(operation);
    }
}
