package elasta.orm.json.sql.criteria;

import elasta.orm.json.exceptions.SqlParameterException;
import io.vertx.core.json.JsonArray;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jango on 9/16/2016.
 */
final public class SqlCriteriaHelper {
    public static String toValueStr(Object val) {
        return val == null ? "" : val.toString();
    }

    public static List<String> toListSql(Object value) {
        if (value == null) {
            return Collections.emptyList();
        }

        if (value instanceof JsonArray) {
            List<String> list = ((JsonArray) value).getList();
            return list.stream().map(o -> o.toString()).collect(Collectors.toList());
        }

        if (value instanceof List) {
            List<String> list = (List<String>) value;
            return list.stream().map(o -> o.toString()).collect(Collectors.toList());
        }

        throw new SqlParameterException("Invalid value given for in operator. value: " + value);
    }
}
