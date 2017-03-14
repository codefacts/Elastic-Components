package elasta.orm.sql.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.intfs.Promise;
import elasta.orm.nm.delete.DeleteData;
import elasta.orm.sql.sql.DbSql;
import elasta.orm.sql.sql.SqlBuilderUtils;
import elasta.orm.sql.sql.SqlExecutor;
import elasta.orm.sql.sql.core.*;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by Jango on 9/25/2016.
 */
public class DbSqlImpl implements DbSql {
    private final SqlExecutor sqlExecutor;
    private final SqlBuilderUtils sqlBuilderUtils;

    public DbSqlImpl(SqlExecutor sqlExecutor, SqlBuilderUtils sqlBuilderUtils) {
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
    public Promise<Void> delete(Collection<DeleteData> deleteDataList) {
        SqlAndParams sqlAndParams = sqlBuilderUtils.deleteSql(deleteDataList);
        return sqlExecutor.update(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<ResultSet> queryWhere(String table, Collection<String> columns, JsonObject whereCriteria) {

        SqlAndParams sqlAndParams = sqlBuilderUtils.querySql(table, columns, whereCriteria);

        return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    public static void main(String[] arg) {
        System.out.println("ok");
    }
}
