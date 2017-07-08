package elasta.orm.query.iml;

import elasta.criteria.json.mapping.JsonToFuncConverter;
import elasta.criteria.json.mapping.JsonToFuncConverterMap;
import elasta.criteria.json.mapping.Mp;
import elasta.orm.query.expression.builder.FieldExpressionHolderFunc;
import elasta.sql.JsonOps;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/21/2017.
 */
final public class CriteriaBuilderJsonToFuncConverterMap implements JsonToFuncConverterMap {
    static final String FIELD = JsonOps.field;
    final JsonToFuncConverterMap jsonToFuncConverterMap;
    final FieldProvider fieldProvider;

    public CriteriaBuilderJsonToFuncConverterMap(JsonToFuncConverterMap jsonToFuncConverterMap, FieldProvider fieldProvider) {
        Objects.requireNonNull(jsonToFuncConverterMap);
        Objects.requireNonNull(fieldProvider);
        this.jsonToFuncConverterMap = jsonToFuncConverterMap;
        this.fieldProvider = fieldProvider;
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
        return (jsonObject, converterMap) -> fieldProvider.field(jsonObject.getString(Mp.arg));
    }

    @Override
    public Map<String, JsonToFuncConverter> getMap() {
        throw new UnsupportedOperationException();
    }

    public interface FieldProvider {
        FieldExpressionHolderFunc field(String fieldExpression);
    }
}
