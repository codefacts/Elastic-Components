package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.intfs.Promise;
import elasta.sql.BaseSqlDB;
import elasta.sql.SqlExecutor;
import elasta.sql.SqlQueryBuilderUtils;
import elasta.sql.core.SqlAndParams;
import elasta.sql.core.SqlQuery;
import elasta.sql.core.UpdateTpl;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.ResultSet;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 6/26/2017.
 */
final public class BaseSqlDBImpl implements BaseSqlDB {
    private final SqlExecutor sqlExecutor;
    private final SqlQueryBuilderUtils sqlQueryBuilderUtils;

    public BaseSqlDBImpl(SqlExecutor sqlExecutor, SqlQueryBuilderUtils sqlQueryBuilderUtils) {
        Objects.requireNonNull(sqlExecutor);
        Objects.requireNonNull(sqlQueryBuilderUtils);
        this.sqlExecutor = sqlExecutor;
        this.sqlQueryBuilderUtils = sqlQueryBuilderUtils;
    }

    @Override
    public Promise<ResultSet> query(SqlQuery sqlQuery) {

        Objects.requireNonNull(sqlQuery);

        SqlAndParams sqlAndParams = sqlQueryBuilderUtils.toSql(sqlQuery);

        return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<Void> update(Collection<UpdateTpl> updateTpls) {

        Objects.requireNonNull(updateTpls);

        ImmutableList.Builder<String> sqlListBuilder = ImmutableList.builder();
        ImmutableList.Builder<JsonArray> paramsListBuilder = ImmutableList.builder();

        updateTpls.stream()
            .map(sqlQueryBuilderUtils::updateSql)
            .forEach(sqlAndParams -> {
                sqlListBuilder.add(sqlAndParams.getSql());
                paramsListBuilder.add(sqlAndParams.getParams());
            });

        return sqlExecutor.update(sqlListBuilder.build(), paramsListBuilder.build());
    }
}
