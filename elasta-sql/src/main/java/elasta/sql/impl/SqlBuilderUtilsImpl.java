package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.criteria.Func;
import elasta.criteria.funcs.ParamsBuilderImpl;
import elasta.sql.SqlBuilderDialect;
import elasta.sql.SqlBuilderUtils;
import elasta.sql.core.*;
import elasta.sql.ex.SqlBuilderUtilsException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

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

        jsonObject.getMap().forEach((column, value) -> {

            keysBuilder.append(column(column, "")).append(COMMA);

            if (value == null) {
                qBuilder.append(nul1()).append(COMMA);
                return;
            }

            qBuilder.append("?").append(COMMA);
            paramsBuilder.add(value);
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
    public SqlListAndParamsList deleteSql(Collection<DeleteData> deleteDataList) {

        ImmutableList.Builder<String> sqlListBuilder = ImmutableList.builder();
        ImmutableList.Builder<JsonArray> paramsListBuilder = ImmutableList.builder();

        deleteDataList.forEach(deleteData -> {

            SqlAndParams sqlAndParams = toDeleteSql(deleteData);
            sqlListBuilder.add(sqlAndParams.getSql());
            paramsListBuilder.add(sqlAndParams.getParams());
        });

        return SqlListAndParamsList.builder()
            .sqlList(sqlListBuilder.build())
            .paramsList(paramsListBuilder.build())
            .build();
    }

    private SqlAndParams toDeleteSql(DeleteData deleteData) {
        ImmutableList.Builder<Object> listBuilder = ImmutableList.builder();

        String where = toWhereSql(deleteData.getColumnValuePairs(), listBuilder);

        return new SqlAndParams(
            "DELETE FROM " + table(deleteData.getTable(), "") + " WHERE " + (where.isEmpty() ? "1" : where),
            new JsonArray(listBuilder.build())
        );
    }

    private String toWhereSql(ColumnValuePair[] columnValuePairs, ImmutableList.Builder<Object> listBuilder) {
        return Arrays.stream(columnValuePairs)
            .map(columnValuePair -> {

                if (columnValuePair.getValue() == null) {

                    return column(columnValuePair.getPrimaryColumn(), "") + " is " + nul1();
                }

                listBuilder.add(columnValuePair.getValue());

                return column(columnValuePair.getPrimaryColumn(), "") + " = ?";

            })
            .collect(Collectors.joining(" AND "));
    }

    @Override
    public SqlAndParams existSql(String table, String primaryKey, Collection<SqlCriteria> sqlCriterias) {
        ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();
        String where = toWhere(sqlCriterias, paramsBuilder);
        return new SqlAndParams(
            "SELECT COUNT(*) FROM " + table(table, "") + " WHERE " + (where.isEmpty() ? "1" : where),
            new JsonArray(
                paramsBuilder.build()
            )
        );
    }

    @Override
    public SqlAndParams deleteSql(DeleteData deleteData) {
        return toDeleteSql(deleteData);
    }

    @Override
    public SqlAndParams toSql(SqlQuery sqlQuery) {

        ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();
        ParamsBuilderImpl paramsBuilder = new ParamsBuilderImpl(paramsListBuilder);

        final StringBuilder builder = new StringBuilder();

        String sql = select(sqlQuery.getSelectFuncs(), paramsBuilder).toSql();

        builder.append("select ").append(sql);

        sql = new FromClauseHandlerImpl(
            ImmutableList.of(
                new JoinClauseHandlerImpl(
                    sqlQuery.getTableAliasPair(),
                    generateJoinData(
                        sqlQuery.getTableAliasPair().getAlias(),
                        sqlQuery.getJoinDatas().stream().collect(Collectors.toMap(JoinData::getAlias, joinData -> joinData))
                    )
                )
            )
        ).toSql();

        builder.append(" from ").append(sql);

        sql = where(sqlQuery.getWhereFuncs(), paramsBuilder).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" where ").append(sql);
        }

        sql = having(sqlQuery.getHavingFuncs(), paramsBuilder).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" having " + sql);
        }

        sql = new OrderByHandlerImpl(sqlQuery.getOrderByDatas()).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" order by ").append(sql);
        }

        sql = new GroupByHandlerImpl(sqlQuery.getColumnAliasPairs()).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" group by " + sql);
        }

        return new SqlAndParams(
            builder.toString(),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    private SelectClauseHandlerImpl select(List<Func> selectFuncs, ParamsBuilderImpl paramsBuilder) {
        return new SelectClauseHandlerImpl(
            selectFuncs,
            paramsBuilder
        );
    }

    private WhereClauseHandlerImpl where(List<Func> whereFuncs, ParamsBuilderImpl paramsBuilder) {
        return new WhereClauseHandlerImpl(
            whereFuncs,
            paramsBuilder
        );
    }

    private HavingClauseHandlerImpl having(List<Func> havingFuncs, ParamsBuilderImpl paramsBuilder) {
        return new HavingClauseHandlerImpl(
            havingFuncs,
            paramsBuilder
        );
    }

    private SqlAndParams createUpdateSql(UpdateTpl updateTpl) {
        ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();

        String set = toSetSql(updateTpl.getData(), paramsListBuilder);

        String where = toWhere(updateTpl.getSqlCriterias(), paramsListBuilder);

        return new SqlAndParams(
            "UPDATE " + table(updateTpl.getTable(), "") + " SET " + set + (where.isEmpty() ? "" : " WHERE " + where),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    private String toSetSql(JsonObject data, ImmutableList.Builder<Object> paramsListBuilder) {

        return data.stream()
            .map(e -> {

                if (e.getValue() == null) {
                    return column(e.getKey(), "") + " = " + nul1();
                }

                paramsListBuilder.add(e.getValue());

                return column(e.getKey(), "") + " = ?";

            })
            .collect(Collectors.joining(", "));
    }

    private SqlAndParams createInsertSql(UpdateTpl updateTpl) {

        if (Utils.not(updateTpl.getSqlCriterias().isEmpty())) {
            throw new SqlBuilderUtilsException("UpdateTpl with operationType 'INSERT' must have an empty SqlCriteria List");
        }

        return insertSql(updateTpl.getTable(), updateTpl.getData());
    }

    private SqlAndParams createDeleteSql(UpdateTpl updateTpl) {

        ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();

        String where = toWhere(updateTpl.getSqlCriterias(), paramsListBuilder);

        return new SqlAndParams(
            "DELETE FROM " + table(updateTpl.getTable(), "") + (where.isEmpty() ? " WHERE 1" : " WHERE " + where),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    private String toWhere(Collection<SqlCriteria> sqlCriterias, ImmutableList.Builder<Object> builder) {
        return sqlCriterias.stream()
            .map(sqlCriteria -> {

                if (sqlCriteria.getValue() == null) {
                    return column(sqlCriteria.getColumn(), sqlCriteria.getAlias().orElse("")) + " is " + nul1();
                }

                builder.add(sqlCriteria.getValue());

                return column(sqlCriteria.getColumn(), sqlCriteria.getAlias().orElse("")) + " = ?";
            })
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
        return whereCriteria.stream()
            .map(e -> {

                if (e.getValue() == null) {
                    return column(e.getKey(), "") + " is " + nul1();
                }

                paramsBuilder.add(e.getValue());

                return column(e.getKey(), "") + " = ?";

            })
            .collect(Collectors.joining(" AND "));
    }

    private String table(String table, String alias) {
        return sqlBuilderDialect.table(table, alias);
    }

    private String column(String column, String alias) {
        return sqlBuilderDialect.column(column, alias);
    }

    private String nul1() {
        return sqlBuilderDialect.nullValue();
    }

    private String toSelectSql(Collection<String> columns) {
        return columns.stream().map(column -> column(column, "")).collect(Collectors.joining(", "));
    }

    private Collection<JoinData> generateJoinData(String rootAlias, Map<String, JoinData> aliasToJoinDataMap) {

        return new JoinDataBuilder(
            rootAlias, aliasToJoinDataMap
        ).build();
    }

    public static void main(String[] asdf) {
        SqlBuilderUtilsImpl sqlBuilderUtils = new SqlBuilderUtilsImpl();

        SqlAndParams sqlAndParams = sqlBuilderUtils.insertSql("table", new JsonObject(new HashMap<>(
                ImmutableMap.of(
                    "name", "sohan",
                    "age", 58,
                    "status", true
                )
            )).putNull("address").putNull("details")
        );

        System.out.println(sqlAndParams);

        SqlListAndParamsList sqlListAndParamsList = sqlBuilderUtils.deleteSql(ImmutableList.of(
            new DeleteData("STUDENT", new ColumnValuePair[]{
                new ColumnValuePair("name", "soan")
            })
        ));

    }
}
