package elasta.sql.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.intfs.Promise;
import elasta.sql.BaseSqlDB;
import elasta.sql.SqlBuilderUtils;
import elasta.sql.SqlExecutor;
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
    private final SqlBuilderUtils sqlBuilderUtils;

    public BaseSqlDBImpl(SqlExecutor sqlExecutor, SqlBuilderUtils sqlBuilderUtils) {
        Objects.requireNonNull(sqlExecutor);
        Objects.requireNonNull(sqlBuilderUtils);
        this.sqlExecutor = sqlExecutor;
        this.sqlBuilderUtils = sqlBuilderUtils;
    }

    @Override
    public Promise<ResultSet> query(SqlQuery sqlQuery) {

        Objects.requireNonNull(sqlQuery);

        SqlAndParams sqlAndParams = sqlBuilderUtils.toSql(sqlQuery);

        return sqlExecutor.query(sqlAndParams.getSql(), sqlAndParams.getParams());
    }

    @Override
    public Promise<Void> update(Collection<UpdateTpl> updateTpls) {

        Objects.requireNonNull(updateTpls);

        ImmutableList.Builder<String> sqlListBuilder = ImmutableList.builder();
        ImmutableList.Builder<JsonArray> paramsListBuilder = ImmutableList.builder();

        updateTpls.stream()
            .map(sqlBuilderUtils::updateSql)
            .forEach(sqlAndParams -> {
                sqlListBuilder.add(sqlAndParams.getSql());
                paramsListBuilder.add(sqlAndParams.getParams());
            });

        return sqlExecutor.update(sqlListBuilder.build(), paramsListBuilder.build());
    }
}
