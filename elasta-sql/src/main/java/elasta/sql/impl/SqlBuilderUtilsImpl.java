package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.criteria.Func;
import elasta.criteria.funcs.FieldFunc;
import elasta.criteria.funcs.ParamsBuilderImpl;
import elasta.criteria.funcs.ops.Ops;
import elasta.sql.SqlBuilderDialect;
import elasta.sql.SqlBuilderUtils;
import elasta.sql.SqlUtils;
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
    public SqlAndParams toSql(SqlQuery sqlQuery) {

        ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();
        ParamsBuilderImpl paramsBuilder = new ParamsBuilderImpl(paramsListBuilder);

        final StringBuilder builder = new StringBuilder();

        String sql = select(sqlQuery.getSelectFuncs(), paramsBuilder).toSql();

        builder.append("SELECT ").append(sql);

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

        builder.append(" FROM ").append(sql);

        sql = where(sqlQuery.getWhereFuncs(), paramsBuilder).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" WHERE ").append(sql);
        }

        sql = having(sqlQuery.getHavingFuncs(), paramsBuilder).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" HAVING " + sql);
        }

        sql = new OrderByHandlerImpl(sqlQuery.getOrderByDatas()).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" ORDER BY ").append(sql);
        }

        sql = new GroupByHandlerImpl(sqlQuery.getGroupBy()).toSql();

        if (not(sql.trim().isEmpty())) {
            builder.append(" GROUP BY " + sql);
        }

        return new SqlAndParams(
            builder.toString(),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    public List<Func> toWhereFuncs(Collection<SqlCriteria> sqlCriterias) {
        ImmutableList.Builder<Func> builder = ImmutableList.builder();

        sqlCriterias.stream()
            .map(sqlCriteria -> Ops.eq(
                new FieldFunc(sqlCriteria.getAlias() + "." + sqlCriteria.getColumn()),
                Ops.valueOfObj(sqlCriteria.getValue())
            ))
            .forEach(builder::add);

        return builder.build();
    }

    public Collection<JoinData> toJoinDatas(String parentAlias, Collection<SqlJoin> sqlJoins) {

        ImmutableList.Builder<JoinData> builder = ImmutableList.builder();

        for (SqlJoin sqlJoin : sqlJoins) {
            builder.add(
                new JoinData(
                    parentAlias,
                    sqlJoin.getJoinType(),
                    sqlJoin.getJoinTable(),
                    sqlJoin.getAlias(),
                    toColumnToColumnMappings(sqlJoin.getAlias(), sqlJoin.getSqlJoinColumns())
                )
            );
        }

        return builder.build();
    }

    public List<Func> toSelectFuncs(Collection<SqlSelection> sqlSelections) {

        ImmutableList.Builder<Func> funcBuilder = ImmutableList.builder();

        sqlSelections.stream().map(sqlSelection -> new FieldFunc(
            sqlSelection.getAlias() + "." + sqlSelection.getColumn()
        )).forEach(funcBuilder::add);

        return funcBuilder.build();
    }

    public List<Func> toWhereFuncs(String rootAlias, Collection<SqlCondition> sqlConditions) {

        ImmutableList.Builder<Func> builder = ImmutableList.builder();

        for (SqlCondition sqlCondition : sqlConditions) {
            builder.add(
                Ops.eq(new FieldFunc(rootAlias + "." + sqlCondition.getColumn()), Ops.valueOfObj(sqlCondition.getValue()))
            );
        }

        return builder.build();
    }

    public List<Func> toWhereFuncs2(String alias, JsonObject whereCriteria) {
        ImmutableList.Builder<Func> builder = ImmutableList.builder();

        whereCriteria.fieldNames().forEach(column -> builder.add(
            Ops.eq(new FieldFunc(alias + "." + column), Ops.valueOfObj(whereCriteria.getValue(column)))
        ));

        return builder.build();
    }

    public List<Func> toSelectFuncs(String alias, Collection<String> selectedColumns) {

        ImmutableList.Builder<Func> builder = ImmutableList.builder();

        selectedColumns.forEach(column -> builder.add(
            new FieldFunc(alias + "." + column)
        ));

        return builder.build();
    }

    public UpdateTpl toDeleteUpdateTpl(String table, JsonObject where) {
        return new UpdateTpl(
            UpdateOperationType.DELETE,
            table,
            SqlUtils.emptyJsonObject(),
            toSqlCriteria(where)
        );
    }

    public UpdateTpl toInsertUpdateTpl(String table, JsonObject jsonObject) {
        return new UpdateTpl(
            UpdateOperationType.INSERT,
            table,
            jsonObject,
            ImmutableList.of()
        );
    }

    public Collection<UpdateTpl> toInsertUpdateTpls(String table, Collection<JsonObject> jsonObjects) {

        ImmutableList.Builder<UpdateTpl> builder = ImmutableList.builder();

        jsonObjects.stream().map(jsonObject -> toInsertUpdateTpl(table, jsonObject)).forEach(builder::add);

        return builder.build();
    }

    private List<ColumnToColumnMapping> toColumnToColumnMappings(String alias, List<SqlJoinColumn> sqlJoinColumns) {
        ImmutableList.Builder<ColumnToColumnMapping> builder = ImmutableList.builder();

        sqlJoinColumns.stream().map(sqlJoinColumn -> new ColumnToColumnMapping(
            alias + "." + sqlJoinColumn.getJoinTableColumn(),
            sqlJoinColumn.getParentTableAlias() + "." + sqlJoinColumn.getParentTableColumn()
        )).forEach(builder::add);

        return builder.build();
    }

    private Collection<SqlCondition> toSqlCriteria(JsonObject where) {
        ImmutableList.Builder<SqlCondition> builder = ImmutableList.builder();
        for (String column : where.fieldNames()) {
            builder.add(
                new SqlCondition(column, where.getValue(column))
            );
        }
        return builder.build();
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

        String where = toWhere(updateTpl.getSqlConditions(), paramsListBuilder);

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

        if (Utils.not(updateTpl.getSqlConditions().isEmpty())) {
            throw new SqlBuilderUtilsException("UpdateTpl with operationType 'INSERT' must have an empty SqlCriteria List");
        }

        return insertSql(updateTpl.getTable(), updateTpl.getData());
    }

    private SqlAndParams createDeleteSql(UpdateTpl updateTpl) {

        ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();

        String where = toWhere(updateTpl.getSqlConditions(), paramsListBuilder);

        return new SqlAndParams(
            "DELETE FROM " + table(updateTpl.getTable(), "") + (where.isEmpty() ? " WHERE 1" : " WHERE " + where),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    private String toWhere(Collection<SqlCondition> sqlCriterias, ImmutableList.Builder<Object> builder) {
        return sqlCriterias.stream()
            .map(sqlCondition -> {

                if (sqlCondition.getValue() == null) {
                    return column(sqlCondition.getColumn(), "") + " IS " + nul1();
                }

                builder.add(sqlCondition.getValue());

                return column(sqlCondition.getColumn(), "") + " = ?";
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

    }
}
