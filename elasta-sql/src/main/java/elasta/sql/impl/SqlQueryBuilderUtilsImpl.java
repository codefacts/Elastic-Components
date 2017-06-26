package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.criteria.Func;
import elasta.criteria.funcs.ParamsBuilderImpl;
import elasta.sql.Cqr;
import elasta.sql.SqlBuilderDialect;
import elasta.sql.SqlQueryBuilderUtils;
import elasta.sql.core.*;
import elasta.sql.ex.SqlBuilderUtilsException;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by sohan on 6/26/2017.
 */
final public class SqlQueryBuilderUtilsImpl implements SqlQueryBuilderUtils {
    final SqlBuilderDialect sqlBuilderDialect;

    public SqlQueryBuilderUtilsImpl(SqlBuilderDialect sqlBuilderDialect) {
        Objects.requireNonNull(sqlBuilderDialect);
        this.sqlBuilderDialect = sqlBuilderDialect;
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

        String sql = toSqlStr(sqlQuery.getSelectFuncs(), paramsBuilder);

        builder.append("SELECT ").append(sql);

        sql = toFromSqlStr(
            ImmutableList.of(
                new JoinClauseHandlerImpl(
                    sqlBuilderDialect,
                    sqlQuery.getTableAliasPair(),
                    generateJoinData(
                        sqlQuery.getTableAliasPair().getAlias(),
                        sqlQuery.getJoinDatas().stream().collect(Collectors.toMap(JoinData::getAlias, joinData -> joinData))
                    )
                )
            )
        );

        builder.append(" FROM ").append(sql);

        sql = toSqlStr(sqlQuery.getWhereFuncs(), paramsBuilder);

        if (not(sql.trim().isEmpty())) {
            builder.append(" WHERE ").append(sql);
        }

        sql = toSqlStr(sqlQuery.getHavingFuncs(), paramsBuilder);

        if (not(sql.trim().isEmpty())) {
            builder.append(" HAVING " + sql);
        }

        sql = toGroupBySqlStr(sqlQuery.getGroupBy());

        if (not(sql.trim().isEmpty())) {
            builder.append(" GROUP BY " + sql);
        }

        sql = toOrderBySqlStr(sqlQuery.getOrderByDatas());

        if (not(sql.trim().isEmpty())) {
            builder.append(" ORDER BY ").append(sql);
        }

        return new SqlAndParams(
            builder.toString(),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    private String toOrderBySqlStr(List<OrderByData> orderByDatas) {
        return orderByDatas.stream()
            .map(orderByData -> column(orderByData.getColumn(), orderByData.getAlias()) + " " + orderByData.getOrder())
            .collect(Collectors.joining(Cqr.COMMA));
    }

    private String toGroupBySqlStr(List<ColumnAliasPair> columnAliasPairs) {
        return columnAliasPairs.stream()
            .map(columnAliasPair -> column(columnAliasPair.getColumn(), columnAliasPair.getAlias()))
            .collect(Collectors.joining(Cqr.COMMA));
    }

    private String toFromSqlStr(ImmutableList<JoinClauseHandlerImpl> joinClauseHandlers) {
        return joinClauseHandlers.stream()
            .map(JoinClauseHandler::toSql).collect(Collectors.joining(", "));
    }

    private String toSqlStr(List<Func> selectFuncs, ParamsBuilderImpl paramsBuilder) {
        return selectFuncs.stream()
            .map(func -> func.get(paramsBuilder))
            .collect(Collectors.joining(", "));
    }

    private Collection<JoinData> generateJoinData(String rootAlias, Map<String, JoinData> aliasToJoinDataMap) {

        return new JoinDataBuilder(
            rootAlias, aliasToJoinDataMap
        ).build();
    }

    private SqlAndParams createUpdateSql(UpdateTpl updateTpl) {
        ImmutableList.Builder<Object> paramsListBuilder = ImmutableList.builder();

        String set = toSetSql(updateTpl.getData(), paramsListBuilder);

        String where = toWhere(updateTpl.getSqlConditions(), paramsListBuilder);

        return new SqlAndParams(
            "UPDATE " + table(updateTpl.getTable()) + " SET " + set + (where.isEmpty() ? "" : " WHERE " + where),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
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
            "DELETE FROM " + table(updateTpl.getTable()) + (where.isEmpty() ? " WHERE 1" : " WHERE " + where),
            new JsonArray(
                paramsListBuilder.build()
            )
        );
    }

    private String toSetSql(JsonObject data, ImmutableList.Builder<Object> paramsListBuilder) {

        return data.stream()
            .map(e -> {

                if (e.getValue() == null) {
                    return column(e.getKey()) + " = " + nul1();
                }

                paramsListBuilder.add(e.getValue());

                return column(e.getKey()) + " = ?";

            })
            .collect(Collectors.joining(", "));
    }

    private String toWhere(Collection<SqlCondition> sqlConditions, ImmutableList.Builder<Object> builder) {
        return sqlConditions.stream()
            .map(sqlCondition -> {

                if (sqlCondition.getValue() == null) {
                    return column(sqlCondition.getColumn()) + " IS " + nul1();
                }

                builder.add(sqlCondition.getValue());

                return column(sqlCondition.getColumn()) + " = ?";
            })
            .collect(Collectors.joining(" AND "));
    }

    private SqlAndParams insertSql(String table, JsonObject jsonObject) {

        String COMMA = ", ";

        ImmutableList.Builder<Object> paramsBuilder = ImmutableList.builder();

        StringBuilder keysBuilder = new StringBuilder();
        StringBuilder qBuilder = new StringBuilder();

        jsonObject.getMap().forEach((column, value) -> {

            keysBuilder.append(column(column)).append(COMMA);

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
            "INSERT INTO " + table(table) + "(" +
                keysBuilder.toString() +
                ") VALUES (" + qBuilder.toString() + ")",
            new JsonArray(paramsBuilder.build())
        );
    }

    private String table(String table, String alias) {
        return sqlBuilderDialect.table(table, alias);
    }

    private String table(String table) {
        return sqlBuilderDialect.table(table);
    }

    private String column(String column, String alias) {
        return sqlBuilderDialect.column(column, alias);
    }

    private String column(String column) {
        return sqlBuilderDialect.column(column);
    }

    private String nul1() {
        return sqlBuilderDialect.nullValue();
    }
}
