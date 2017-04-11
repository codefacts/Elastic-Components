package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.intfs.Promise;
import elasta.sql.SqlBuilderUtils;
import elasta.sql.core.*;
import elasta.sql.SqlDB;
import elasta.sql.SqlExecutor;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 9/25/2016.
 */
public class SqlDBImpl implements SqlDB {
    private final SqlExecutor sqlExecutor;
    private final SqlBuilderUtils sqlBuilderUtils;

    public SqlDBImpl(SqlExecutor sqlExecutor, SqlBuilderUtils sqlBuilderUtils) {
        Objects.requireNonNull(sqlExecutor);
        Objects.requireNonNull(sqlBuilderUtils);
        this.sqlExecutor = sqlExecutor;
        this.sqlBuilderUtils = sqlBuilderUtils;
    }

    @Override
    public Promise<ResultSet> query(Collection<SqlSelection> sqlSelections, SqlFrom sqlFrom, Collection<SqlJoin> sqlJoins, Collection<SqlCriteria> sqlCriterias) {
        SqlAndParams sqlAndParams = sqlBuilderUtils.querySql(sqlSelections, sqlFrom, sqlJoins, sqlCriterias);
        return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<Void> insertJo(String table, JsonObject jsonObject) {
        SqlAndParams sqlAndParams = sqlBuilderUtils.insertSql(table, jsonObject);
        return sqlExecutor.update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<Void> insertJo(String table, Collection<JsonObject> sqlList) {
        ImmutableList.Builder<String> sqlListBuilder = ImmutableList.builder();
        ImmutableList.Builder<JsonArray> paramsListBuilder = ImmutableList.builder();

        sqlList.stream().map(val -> sqlBuilderUtils.insertSql(table, val))
            .forEach(insertSql -> {
                sqlListBuilder.add(insertSql.getSql());
                paramsListBuilder.add(insertSql.getParams());
            });

        return sqlExecutor.update(sqlListBuilder.build(), paramsListBuilder.build());
    }

    @Override
    public Promise<Void> update(Collection<UpdateTpl> sqlList) {

        ImmutableList.Builder<String> sqlListBuilder = ImmutableList.builder();
        ImmutableList.Builder<JsonArray> paramsListBuilder = ImmutableList.builder();

        sqlList.stream()
            .map(sqlBuilderUtils::updateSql)
            .forEach(sqlAndParams -> {
                sqlListBuilder.add(sqlAndParams.getSql());
                paramsListBuilder.add(sqlAndParams.getParams());
            });

        return sqlExecutor.update(sqlListBuilder.build(), paramsListBuilder.build());
    }

    @Override
    public Promise<Void> delete(String table, JsonObject where) {
        SqlAndParams sqlAndParams = sqlBuilderUtils.deleteSql(table, where);
        return sqlExecutor.update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<Void> delete(DeleteData deleteData) {
        SqlAndParams sqlAndParams = sqlBuilderUtils.deleteSql(deleteData);
        return sqlExecutor.update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<Void> delete(Collection<DeleteData> deleteDatas) {
        SqlListAndParamsList sqlListAndParamsList = sqlBuilderUtils.deleteSql(deleteDatas);
        return sqlExecutor.update(sqlListAndParamsList.getSqlList(), sqlListAndParamsList.getParamsList());
    }

    @Override
    public Promise<ResultSet> query(String table, Collection<String> columns, JsonObject whereCriteria) {

        SqlAndParams sqlAndParams = sqlBuilderUtils.querySql(table, columns, whereCriteria);

        return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<Boolean> exists(String table, String primaryKey, Collection<SqlCriteria> sqlCriterias) {
        SqlAndParams sqlAndParams = sqlBuilderUtils.existSql(table, primaryKey, sqlCriterias);
        return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams())
            .map(
                resultSet -> resultSet.getResults().stream()
                    .limit(1)
                    .map(objects -> objects.getLong(0))
                    .map(count -> count > 0)
                    .findAny()
                    .orElse(false)
            );
    }

    @Override
    public Promise<ResultSet> query(SqlQuery sqlQuery) {

        SqlAndParams sqlAndParams = sqlBuilderUtils.toSql(sqlQuery);

        return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    public static void main(String[] arg) {
        System.out.println("ok");
    }
}
