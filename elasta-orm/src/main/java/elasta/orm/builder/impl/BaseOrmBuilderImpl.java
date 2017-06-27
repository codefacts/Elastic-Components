package elasta.orm.builder.impl;

import elasta.orm.BaseOrm;
import elasta.orm.builder.BaseOrmBuilder;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Entity;
import elasta.orm.impl.BaseOrmImpl;
import elasta.orm.query.QueryExecutor;
import elasta.orm.upsert.IdGenerator;
import elasta.sql.SqlDB;

import java.util.*;

/**
 * Created by sohan on 3/19/2017.
 */
final public class BaseOrmBuilderImpl implements BaseOrmBuilder {
    final EntityMappingHelper helper;
    final SqlDB sqlDB;
    final QueryExecutor queryExecutor;
    final IdGenerator idGenerator;

    public BaseOrmBuilderImpl(EntityMappingHelper helper, SqlDB sqlDB, QueryExecutor queryExecutor, IdGenerator idGenerator) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(queryExecutor);
        Objects.requireNonNull(idGenerator);
        this.helper = helper;
        this.sqlDB = sqlDB;
        this.queryExecutor = queryExecutor;
        this.idGenerator = idGenerator;
    }

    @Override
    public BaseOrm build(Collection<Entity> entities) {
        return new BaseOrmImpl(
            new OperationMapBuilder(entities, helper, sqlDB, idGenerator).build(),
            queryExecutor
        );
    }
}
