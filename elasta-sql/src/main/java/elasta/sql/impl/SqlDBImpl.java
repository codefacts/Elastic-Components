package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.criteria.funcs.FieldFunc;
import elasta.sql.BaseSqlDB;
import elasta.sql.SqlBuilderUtils;
import elasta.sql.core.*;
import elasta.sql.SqlDB;
import elasta.sql.dbaction.DbInterceptors;
import elasta.sql.ex.SqlDbException;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.sql.SQLDataException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

/**
 * Created by Jango on 9/25/2016.
 */
final public class SqlDBImpl implements SqlDB {
    final BaseSqlDB baseSqlDB;
    final SqlBuilderUtils sqlBuilderUtils;
    final DbInterceptors dbInterceptors;

    public SqlDBImpl(BaseSqlDB baseSqlDB, SqlBuilderUtils sqlBuilderUtils, DbInterceptors dbInterceptors) {
        Objects.requireNonNull(baseSqlDB);
        Objects.requireNonNull(sqlBuilderUtils);
        Objects.requireNonNull(dbInterceptors);
        this.baseSqlDB = baseSqlDB;
        this.sqlBuilderUtils = sqlBuilderUtils;
        this.dbInterceptors = dbInterceptors;
    }

    @Override
    public Promise<ResultSet> query(Collection<SqlSelection> sqlSelections, SqlFrom sqlFrom, Collection<SqlJoin> sqlJoins, Collection<SqlCriteria> sqlCriterias) {

        return querySql(
            SqlQuery.builder()
                .selectFuncs(sqlBuilderUtils.toSelectFuncs(sqlSelections))
                .tableAliasPair(new TableAliasPair(sqlFrom.getTable(), sqlFrom.getAlias()))
                .joinDatas(sqlBuilderUtils.toJoinDatas(sqlFrom.getAlias(), sqlJoins))
                .whereFuncs(sqlBuilderUtils.toWhereFuncs(sqlCriterias))
                .build()
        );
    }

    @Override
    public Promise<ResultSet> query(String table, Collection<String> selectedColumns, JsonObject whereCriteria) {

        final String alias = "r";

        return querySql(
            SqlQuery.builder()
                .selectFuncs(sqlBuilderUtils.toSelectFuncs(alias, selectedColumns))
                .tableAliasPair(new TableAliasPair(table, alias))
                .whereFuncs(sqlBuilderUtils.toWhereFuncs2(alias, whereCriteria))
                .build()
        );
    }

    @Override
    public Promise<Boolean> exists(String table, String primaryKey, Collection<SqlCondition> sqlConditions) {

        final String rootAlias = "r";

        return baseSqlDB
            .query(
                SqlQuery.builder()
                    .selectFuncs(ImmutableList.of(new FieldFunc(rootAlias + "." + primaryKey)))
                    .tableAliasPair(new TableAliasPair(table, rootAlias))
                    .whereFuncs(sqlBuilderUtils.toWhereFuncs(rootAlias, sqlConditions))
                    .build()
            )
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
        return querySql(sqlQuery);
    }

    private Promise<ResultSet> querySql(SqlQuery sqlQuery) {
        return dbInterceptors.interceptQuery(sqlQuery)
            .mapP(baseSqlDB::query);
    }

    @Override
    public Promise<Void> insertJo(String table, JsonObject jsonObject) {

        return exeUpdate(
            ImmutableList.of(
                sqlBuilderUtils.toInsertUpdateTpl(table, jsonObject)
            )
        );
    }

    @Override
    public Promise<Void> insertJo(String table, Collection<JsonObject> jsonObjects) {
        return exeUpdate(
            sqlBuilderUtils.toInsertUpdateTpls(table, jsonObjects)
        );
    }

    @Override
    public Promise<Void> update(Collection<UpdateTpl> updateTpls) {
        return exeUpdate(updateTpls);
    }

    private Promise<Void> exeUpdate(Collection<UpdateTpl> updateTpls) {

        if (updateTpls.isEmpty()) {
            throw new SqlDbException("UpdateTpls can not be empty");
        }

        ImmutableList.Builder<UpdateTpl> builder = ImmutableList.builder();

        Promise<UpdateTpl> promise = Promises.empty();

        for (Iterator<UpdateTpl> iterator = updateTpls.iterator(); iterator.hasNext(); ) {

            promise = promise
                .map(tpl -> iterator.next())
                .mapP(dbInterceptors::interceptUpdate)
                .then(builder::add)
            ;
        }

        return promise.mapP(updateTpl -> baseSqlDB.update(builder.build()));
    }

    @Override
    public Promise<Void> delete(String table, JsonObject where) {
        return exeUpdate(ImmutableList.of(
            sqlBuilderUtils.toDeleteUpdateTpl(table, where)
        ));
    }

    public static void main(String[] arg) {
        System.out.println("ok");
    }
}
