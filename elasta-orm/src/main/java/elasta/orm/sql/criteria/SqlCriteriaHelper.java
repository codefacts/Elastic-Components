package elasta.orm.sql.criteria;

import elasta.orm.exceptions.SqlParameterException;
import io.vertx.core.json.JsonArray;

import java.util.List;

/**
 * Created by Jango on 9/16/2016.
 */
final public class SqlCriteriaHelper {
    public static String toValueStr(Object val) {
        return val == null ? "" : val.getClass() == String.class ? "'" + val + "'" : val.toString();
    }

    public static String toListSql(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof JsonArray) {
            return toListSqlII(((JsonArray) value).getList());
        }

        if (value instanceof List) {
            return toListSqlII((List) value);
        }

        throw new SqlParameterException("Invalid value given for in operator. value: " + value);
    }

    private static String toListSqlII(List list) {
        if (list.isEmpty()) return "";
        final String COMMA = ", ";

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("(");

        list.forEach(o -> {
            stringBuilder.append(toValueStr(o)).append(COMMA);
        });

        stringBuilder.delete(stringBuilder.length() - COMMA.length(), stringBuilder.length());

        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}
