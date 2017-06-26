package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.intfs.Promise;
import elasta.criteria.funcs.FieldFunc;
import elasta.sql.BaseSqlDB;
import elasta.sql.SqlBuilderUtils;
import elasta.sql.core.*;
import elasta.sql.SqlDB;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.ResultSet;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by Jango on 9/25/2016.
 */
final public class SqlDBImpl implements SqlDB {
    final BaseSqlDB baseSqlDB;
    final SqlBuilderUtils sqlBuilderUtils;

    public SqlDBImpl(BaseSqlDB baseSqlDB, SqlBuilderUtils sqlBuilderUtils) {
        Objects.requireNonNull(baseSqlDB);
        Objects.requireNonNull(sqlBuilderUtils);
        this.baseSqlDB = baseSqlDB;
        this.sqlBuilderUtils = sqlBuilderUtils;
    }

    @Override
    public Promise<ResultSet> query(Collection<SqlSelection> sqlSelections, SqlFrom sqlFrom, Collection<SqlJoin> sqlJoins, Collection<SqlCriteria> sqlCriterias) {

        return baseSqlDB.query(
            SqlQuery.builder()
                .selectFuncs(sqlBuilderUtils.toSelectFuncs(sqlSelections))
                .tableAliasPair(new TableAliasPair(sqlFrom.getTable(), sqlFrom.getAlias()))
                .joinDatas(sqlBuilderUtils.toJoinDatas(sqlFrom.getAlias(), sqlJoins))
                .whereFuncs(sqlBuilderUtils.toWhereFuncs(sqlCriterias))
                .build()
        );
    }

    @Override
    public Promise<Void> insertJo(String table, JsonObject jsonObject) {

        return baseSqlDB.update(
            ImmutableList.of(
                sqlBuilderUtils.toInsertUpdateTpl(table, jsonObject)
            )
        );
    }

    @Override
    public Promise<Void> insertJo(String table, Collection<JsonObject> jsonObjects) {
        return baseSqlDB.update(
            sqlBuilderUtils.toInsertUpdateTpls(table, jsonObjects)
        );
    }

    @Override
    public Promise<Void> update(Collection<UpdateTpl> updateTpls) {
        return baseSqlDB.update(updateTpls);
    }

    @Override
    public Promise<Void> delete(String table, JsonObject where) {
        return baseSqlDB.update(ImmutableList.of(
            sqlBuilderUtils.toDeleteUpdateTpl(table, where)
        ));
    }

    @Override
    public Promise<ResultSet> query(String table, Collection<String> selectedColumns, JsonObject whereCriteria) {

        final String alias = "r";

        return baseSqlDB.query(
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
        return baseSqlDB.query(sqlQuery);
    }

    public static void main(String[] arg) {
        System.out.println("ok");
    }
}
