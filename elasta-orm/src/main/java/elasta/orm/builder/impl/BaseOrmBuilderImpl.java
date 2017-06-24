package elasta.orm.builder.impl;

import elasta.orm.BaseOrm;
import elasta.orm.builder.BaseOrmBuilder;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Entity;
import elasta.sql.dbaction.DbInterceptors;
import elasta.orm.impl.BaseOrmImpl;
import elasta.orm.query.QueryExecutor;
import elasta.sql.SqlDB;

import java.util.*;

/**
 * Created by sohan on 3/19/2017.
 */
final public class BaseOrmBuilderImpl implements BaseOrmBuilder {
    final EntityMappingHelper helper;
    final SqlDB sqlDB;
    final QueryExecutor queryExecutor;

    public BaseOrmBuilderImpl(EntityMappingHelper helper, SqlDB sqlDB, QueryExecutor queryExecutor) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(queryExecutor);
        this.helper = helper;
        this.sqlDB = sqlDB;
        this.queryExecutor = queryExecutor;
    }

    @Override
    public BaseOrm build(Collection<Entity> entities) {
        return new BaseOrmImpl(
            new OperationMapBuilder(entities, helper, sqlDB).build(),
            queryExecutor
        );
    }
}
