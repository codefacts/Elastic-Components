package elasta.orm.json.sql;

import com.google.common.collect.ImmutableList;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

/**
 * Created by Jango on 10/12/2016.
 */
public class SqlBuilderUtilsImpl implements SqlBuilderUtils {
    @Override
    public SqlAndParams insertSql(String table, JsonObject jsonObject) {

        String COMMA = ", ";

        ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();

        StringBuilder keysBuilder = new StringBuilder();
        StringBuilder qBuilder = new StringBuilder();

        jsonObject.getMap().forEach((key, o) -> {
            keysBuilder.append("`").append(key).append("`").append(COMMA);
            qBuilder.append("?").append(COMMA);
            paramsBuilder.add(o);
        });

        if (jsonObject.size() > 0) {
            keysBuilder.delete(keysBuilder.length() - COMMA.length(), keysBuilder.length());
            qBuilder.delete(qBuilder.length() - COMMA.length(), qBuilder.length());
        }

        return new SqlAndParams(
            "INSERT INTO `" + table + "`(" +
                keysBuilder.toString() +
                ") VALUES (" + qBuilder.toString() + ")",
            new JsonArray(paramsBuilder.build())
        );
    }

    @Override
    public SqlAndParams updateSql(String table, JsonObject jsonObject, String where, JsonArray params) {

        String COMMA = ", ";

        ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();

        StringBuilder keysBuilder = new StringBuilder();

        jsonObject.getMap().forEach((key, o) -> {
            keysBuilder.append("`").append(key).append("`").append(" = ").append("?").append(COMMA);
            paramsBuilder.add(o);
        });

        if (jsonObject.size() > 0) {
            keysBuilder.delete(keysBuilder.length() - COMMA.length(), keysBuilder.length());
        }

        return new SqlAndParams(
            "UPDATE `" + table + "` SET " + keysBuilder.toString() + " WHERE " + where,
            new JsonArray(paramsBuilder.addAll(params.getList()).build())
        );
    }

    @Override
    public SqlAndParams deleteSql(String table, String where, JsonArray jsonArray) {
        return new SqlAndParams("DELETE FROM `" + table + "` WHERE " + where, jsonArray);
    }
}
