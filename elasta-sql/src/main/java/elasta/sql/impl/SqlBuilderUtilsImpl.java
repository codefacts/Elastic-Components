package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.sql.SqlBuilderDialect;
import elasta.sql.SqlBuilderUtils;
import elasta.sql.core.*;
import elasta.sql.ex.SqlBuilderUtilsException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 10/12/2016.
 */
final public class SqlBuilderUtilsImpl implements SqlBuilderUtils {
    final SqlBuilderDialect sqlBuilderDialect;

    public SqlBuilderUtilsImpl() {
        this(new MySqlSqlBuilderDialectImpl());
    }

    public SqlBuilderUtilsImpl(SqlBuilderDialect sqlBuilderDialect) {
        Objects.requireNonNull(sqlBuilderDialect);
        this.sqlBuilderDialect = sqlBuilderDialect;
    }

    @Override
    public SqlAndParams insertSql(String table, JsonObject jsonObject) {

        String COMMA = ", ";

        ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();

        StringBuilder keysBuilder = new StringBuilder();
        StringBuilder qBuilder = new StringBuilder();

        jsonObject.getMap().forEach((column, o) -> {
            keysBuilder.append(column(column, "")).append(COMMA);
            qBuilder.append("?").append(COMMA);
            paramsBuilder.add(o);
        });

        if (jsonObject.size() > 0) {
            keysBuilder.delete(keysBuilder.length() - COMMA.length(), keysBuilder.length());
            qBuilder.delete(qBuilder.length() - COMMA.length(), qBuilder.length());
        }

        return new SqlAndParams(
            "INSERT INTO " + table(table, "") + "(" +
                keysBuilder.toString() +
                ") VALUES (" + qBuilder.toString() + ")",
            new JsonArray(paramsBuilder.build())
        );
    }

    @Override
    public SqlAndParams querySql(String table, Collection<String> columns, JsonObject whereCriteria) {
        ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();
        String selectSql = toSelectSql(columns);
        String whereSql = toWhereSql(whereCriteria, paramsBuilder);
        return new SqlAndParams(
            "SELECT " + selectSql +
                " FROM " + table(table, "")
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

    @Override
    public SqlAndParams updateSql(UpdateTpl updateTpl) {

        switch (updateTpl.getUpdateOperationType()) {
            case DELETE: {
                return createDeleteSql(updateTpl);
            }
            case INSERT: {
                return createInsertSql(updateTpl);
            }
            case UPDATE: {
                return createUpdateSql(updateTpl);
            }
        }
        throw new SqlBuilderUtilsException("Invalid updateOperationType '" + updateTpl.getUpdateOperationType() + "'");
    }

    @Override
    public SqlAndParams deleteSql(String table, JsonObject where) {
        ImmutableList.Builder<Object> builder = ImmutableList.builder();
        String whereSql = toWhereSql(where, builder);
        return new SqlAndParams(
            "DELETE FROM " + table(table, "") + (whereSql.isEmpty() ? " WHERE 1" : " WHERE " + whereSql),
            new JsonArray(builder.build())
        );
    }

    @Override
    public SqlAndParams deleteSql(Collection<DeleteData> deleteDataList) {
        return null;
    }

    private SqlAndParams createUpdateSql(UpdateTpl updateTpl) {
        ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();

        String set = updateTpl.getData()
            .stream()
            .peek(e -> paramsListBuilder.add(e.getValue()))
            .map(e -> column(e.getKey(), "") + " = ?")
            .collect(Collectors.joining(", "));

        String where = updateTpl.getSqlCriterias().stream()
            .peek(sqlCriteria -> paramsListBuilder.add(sqlCriteria.getValue()))
            .map(sqlCriteria -> column(sqlCriteria.getColumn(), "") + " = ?")
            .collect(Collectors.joining(" AND "));

        return new SqlAndParams(
            "UPDATE " + table(updateTpl.getTable(), "") + " SET " + set + (where.isEmpty() ? "" : " WHERE " + where),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    private SqlAndParams createInsertSql(UpdateTpl updateTpl) {

        StringBuilder columnsBuilder = new StringBuilder();
        StringBuilder valuesBuilder = new StringBuilder();

        ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();

        final String COMMA = ", ";

        updateTpl.getData().forEach(e -> {
            columnsBuilder.append(column(e.getKey(), "")).append(COMMA);
            valuesBuilder.append("?").append(COMMA);
            paramsListBuilder.add(
                e.getValue()
            );
        });

        if (updateTpl.getData().size() > 0) {
            columnsBuilder.delete(columnsBuilder.length() - COMMA.length(), columnsBuilder.length());
            valuesBuilder.delete(valuesBuilder.length() - COMMA.length(), valuesBuilder.length());
        }

        return new SqlAndParams(
            "INSERT INTO " + table(updateTpl.getTable(), "") + " (" + columnsBuilder.toString() + ") VALUES (" + valuesBuilder.toString() + ")",
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    private SqlAndParams createDeleteSql(UpdateTpl updateTpl) {

        ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();

        String where = updateTpl.getSqlCriterias().stream()
            .peek(sqlCriteria -> paramsListBuilder.add(sqlCriteria.getValue()))
            .map(sqlCriteria -> column(sqlCriteria.getColumn(), "") + " = ?")
            .collect(Collectors.joining(" AND "));

        return new SqlAndParams(
            "DELETE FROM " + table(updateTpl.getTable(), "") + (where.isEmpty() ? " WHERE 1" : " WHERE " + where),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    private String toWhere(Collection<SqlCriteria> sqlCriterias, ImmutableList.Builder<Object> builder) {
        return sqlCriterias.stream().peek(sqlCriteria -> builder.add(sqlCriteria.getValue()))
            .map(sqlCriteria -> column(sqlCriteria.getColumn(), sqlCriteria.getAlias().orElse("")) + " = ?")
            .collect(Collectors.joining(" AND "));
    }

    private String toSqlJoin(Collection<SqlJoin> sqlJoins) {
        return sqlJoins.stream()
            .map(
                sqlJoin -> sqlJoin.getJoinType().getValue().toUpperCase()
                    + " " + table(sqlJoin.getJoinTable(), sqlJoin.getAlias().orElse(""))
                    + " ON "
                    + sqlJoin
                    .getSqlJoinColumns()
                    .stream()
                    .map(
                        sqlJoinColumn ->
                            column(sqlJoinColumn.getJoinTableColumn(), sqlJoin.getAlias().orElse("")) + " = "
                                + column(sqlJoinColumn.getParentTableColumn(), sqlJoinColumn.getParentTableAlias().orElse(""))
                    ).collect(Collectors.joining(" AND "))
            )
            .collect(Collectors.joining(" "));
    }

    private String toSqlFrom(SqlFrom sqlFrom) {
        return table(sqlFrom.getTable(), sqlFrom.getAlias().orElse(""));
    }

    private String toSqlSelect(Collection<SqlSelection> sqlSelections) {
        return sqlSelections.stream()
            .map(
                sqlSelection -> column(sqlSelection.getColumn(), sqlSelection.getAlias().orElse(""))
            )
            .collect(Collectors.joining(", "));
    }

    private String toWhereSql(JsonObject whereCriteria, ImmutableList.Builder<Object> paramsBuilder) {
        return whereCriteria.stream().peek(e -> paramsBuilder.add(e.getValue()))
            .map(e -> column(e.getKey(), "") + " = ?").collect(Collectors.joining(" AND "));
    }

    private String table(String table, String alias) {
        return sqlBuilderDialect.table(table, alias);
    }

    private String column(String column, String alias) {
        return sqlBuilderDialect.column(column, alias);
    }

    private String toSelectSql(Collection<String> columns) {
        return columns.stream().map(column -> column(column, "")).collect(Collectors.joining(", "));
    }
}
