package test;

import com.google.common.collect.ImmutableMap;
import elasta.criteria.funcs.ops.impl.ComparisionOpsImpl;
import elasta.criteria.funcs.ops.impl.LogicalOpsImpl;
import elasta.criteria.funcs.ops.impl.ValueHolderOpsImpl;
import elasta.criteria.json.mapping.GenericJsonToFuncConverterImpl;
import elasta.criteria.json.mapping.JsonToFuncConverterMap;
import elasta.criteria.json.mapping.impl.JsonToFuncConverterBuilderImpl;
import elasta.criteria.json.mapping.impl.JsonToFuncConverterMapBuilderImpl;
import elasta.criteria.json.mapping.impl.ValueHolderOperationBuilderImpl;
import elasta.orm.BaseOrm;
import elasta.orm.builder.impl.BaseOrmBuilderImpl;
import elasta.orm.entity.EntitiesPreprocessor;
import elasta.orm.entity.EntitiesValidator;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.EntityUtils;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.impl.SimpleDbColumnMappingImpl;
import elasta.orm.entity.impl.EntitiesPreprocessorImpl;
import elasta.orm.entity.impl.EntitiesValidatorImpl;
import elasta.orm.entity.impl.EntityMappingHelperImpl;
import elasta.orm.entity.impl.EntityValidatorImpl;
import elasta.orm.query.iml.QueryExecutorImpl;
import elasta.sql.SqlDB;
import elasta.sql.SqlExecutor;
import elasta.sql.impl.SqlBuilderUtilsImpl;
import elasta.sql.impl.SqlDBImpl;
import elasta.sql.impl.SqlExecutorImpl;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import lombok.Builder;
import lombok.Value;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Created by sohan on 3/22/2017.
 */
public interface Test {

    static EntityMappingHelper helper(Collection<Entity> entities) {
        return new EntityMappingHelperImpl(
            EntityUtils.toEntityNameToEntityMap(entities),
            EntityUtils.toTableToEntityMap(entities)
        );
    }

    static SqlDB dbSql(SqlExecutor sqlExecutor) {
        final SqlDBImpl dbSql = new SqlDBImpl(
            sqlExecutor,
            new SqlBuilderUtilsImpl()
        );
        return dbSql;
    }

    static JDBCClient jdbcClient(String db, Vertx vertx) {
        return JDBCClient.createShared(vertx, new JsonObject(
            ImmutableMap.of(
                "user", "root",
                "password", "",
                "driver_class", "com.mysql.jdbc.Driver",
                "url", "jdbc:mysql://localhost/" + db
            )
        ));
    }

    static BaseOrm baseOrm(Params params) {

        validate(params.entities);

        params = new Params(
            process(params.entities),
            params.jdbcClient
        );

        EntityMappingHelper helper = helper(params.entities);

        SqlExecutor sqlExecutor = sqlExecutor(params.jdbcClient);

        return new BaseOrmBuilderImpl(
            helper,
            dbSql(sqlExecutor),
            new QueryExecutorImpl(
                helper,
                jsonToFuncConverterMap(),
                new GenericJsonToFuncConverterImpl(),
                sqlExecutor
            )
        ).build(params.entities);
    }

    static void validate(Collection<Entity> entities) {
        new EntitiesValidatorImpl(new EntityValidatorImpl()).validate(
            EntitiesValidator.Params.builder()
                .entities(entities)
                .entityNameToEntityMap(EntityUtils.toEntityNameToEntityMap(entities))
                .tableToTableDependencyMap(EntityUtils.toTableToTableDependencyMap(entities).getTableToTableDependencyMap())
                .build()
        );
    }

    static List<Entity> process(Collection<Entity> entities) {
        return new EntitiesPreprocessorImpl()
            .process(EntitiesPreprocessor.Params.builder()
                .entities(entities)
                .entityNameToEntityMap(EntityUtils.toEntityNameToEntityMap(entities))
                .tableToTableDependencyMap(EntityUtils.toTableToTableDependencyMap(entities).getTableToTableDependencyMap())
                .build());
    }

    static SqlExecutor sqlExecutor(JDBCClient jdbcClient) {
        return new SqlExecutorImpl(
            jdbcClient
        );
    }

    static JsonToFuncConverterMap jsonToFuncConverterMap() {
        return new JsonToFuncConverterMapBuilderImpl(
            new LogicalOpsImpl(),
            new ComparisionOpsImpl(),
            new ValueHolderOpsImpl(),
            new JsonToFuncConverterBuilderImpl(
                new ValueHolderOperationBuilderImpl(
                    new ValueHolderOpsImpl()
                )
            )
        ).build();
    }

    @Value
    @Builder
    static class Params {
        final Collection<Entity> entities;
        final JDBCClient jdbcClient;
    }
}
