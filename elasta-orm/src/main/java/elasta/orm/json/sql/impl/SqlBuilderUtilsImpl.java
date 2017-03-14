package elasta.orm.json.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.json.sql.*;
import elasta.orm.json.sql.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

        jsonObject.getMap().forEach((column, o) -> {
            keysBuilder.append(column(column)).append(COMMA);
            qBuilder.append("?").append(COMMA);
            paramsBuilder.add(o);
        });

        if (jsonObject.size() > 0) {
            keysBuilder.delete(keysBuilder.length() - COMMA.length(), keysBuilder.length());
            qBuilder.delete(qBuilder.length() - COMMA.length(), qBuilder.length());
        }

        return new SqlAndParams(
            "INSERT INTO " + table(table) + "(" +
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
            keysBuilder.append(column(key)).append(" = ").append("?").append(COMMA);
            paramsBuilder.add(o);
        });

        if (jsonObject.size() > 0) {
            keysBuilder.delete(keysBuilder.length() - COMMA.length(), keysBuilder.length());
        }

        return new SqlAndParams(
            "UPDATE " + table(table) + " SET " + keysBuilder.toString() + " WHERE " + where,
            new JsonArray(paramsBuilder.addAll(params.getList()).build())
        );
    }

    @Override
    public SqlAndParams deleteSql(String table, String where, JsonArray jsonArray) {
        return new SqlAndParams("DELETE FROM " + table(table) + " WHERE " + where, jsonArray);
    }

    @Override
    public SqlAndParams querySql(String table, List<String> columns, JsonObject whereCriteria) {
        ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();
        String selectSql = toSelectSql(columns);
        String whereSql = toWhereSql(whereCriteria, paramsBuilder);
        return new SqlAndParams(
            "SELECT " + selectSql +
                " FROM " + table(table)
                + (whereSql.isEmpty() ? "" : " WHERE " + whereSql)
            ,
            new JsonArray(paramsBuilder.build())
        );
    }

    @Override
    public SqlAndParams querySql(Collection<SqlSelection> sqlSelections, SqlFrom sqlFrom, Collection<SqlJoin> sqlJoins, Collection<SqlCriteria> sqlCriterias) {
        String select = toSqlSelect(sqlSelections);
        String from = toSqlFrom(sqlFrom);
        String join = toSqlJoin(sqlJoins);
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        String whereSql = toWhere(sqlCriterias, builder);
        return new SqlAndParams(
            "SELECT " + select + " FROM " + from + join + (whereSql.isEmpty() ? "" : " WHERE " + whereSql),
            new JsonArray(builder.build())
        );
    }

    private String toWhere(Collection<SqlCriteria> sqlCriterias, ImmutableList.Builder<Object> builder) {
        return sqlCriterias.stream().peek(sqlCriteria -> builder.add(sqlCriteria.getValue()))
            .map(sqlCriteria -> sqlCriteria.getAlias().map(alias -> alias + ".").orElse("") + column(sqlCriteria.getColumn()) + " = ?")
            .collect(Collectors.joining(" AND "));
    }

    private String toSqlJoin(Collection<SqlJoin> sqlJoins) {
        return sqlJoins.stream()
            .map(
                sqlJoin -> sqlJoin.getJoinType().getValue().toUpperCase()
                    + " " + table(sqlJoin.getJoinTable())
                    + sqlJoin.getAlias().map(alias -> " " + alias)
                    + " ON "
                    + sqlJoin
                    .getSqlJoinColumns()
                    .stream()
                    .map(
                        sqlJoinColumn -> sqlJoin.getAlias().map(alias -> alias + ".").orElse("")
                            + column(sqlJoinColumn.getJoinTableColumn()) + " = "
                            + sqlJoinColumn.getParentTableAlias().map(alias -> alias + ".").orElse("") + column(sqlJoinColumn.getParentTableColumn())
                    ).collect(Collectors.joining(" AND "))
            )
            .collect(Collectors.joining(" "));
    }

    private String toSqlFrom(SqlFrom sqlFrom) {
        return table(sqlFrom.getTable()) + sqlFrom.getAlias().map(alias -> " " + alias).orElse("");
    }

    private String toSqlSelect(Collection<SqlSelection> sqlSelections) {
        return sqlSelections.stream()
            .map(
                sqlSelection ->
                    sqlSelection
                        .getAlias()
                        .map(alias -> alias + ".")
                        .orElse("")
                        + column(sqlSelection.getColumn())
            )
            .collect(Collectors.joining(", "));
    }

    private String table(String table) {
        return "`" + table + "`";
    }

    private String toWhereSql(JsonObject whereCriteria, ImmutableList.Builder<Object> paramsBuilder) {
        return whereCriteria.stream().peek(e -> paramsBuilder.add(e.getValue()))
            .map(e -> column(e.getKey()) + " = ?").collect(Collectors.joining(" AND "));
    }

    private String column(String column) {
        return "`" + column + "`";
    }

    private String toSelectSql(List<String> columns) {
        return columns.stream().map(this::column).collect(Collectors.joining(", "));
    }
}
