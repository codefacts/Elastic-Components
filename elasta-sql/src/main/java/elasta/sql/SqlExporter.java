package elasta.sql;

import com.google.common.collect.ImmutableList;
import elasta.module.ModuleExporter;
import elasta.module.ModuleSystemBuilder;
import elasta.sql.dbaction.DbInterceptors;
import elasta.sql.dbaction.impl.DbInterceptorsImpl;
import elasta.sql.impl.*;
import io.vertx.ext.jdbc.JDBCClient;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 6/28/2017.
 */
public interface SqlExporter extends ModuleExporter {

    static ModuleSystemBuilder exportTo(ExportToParams config) {
        Objects.requireNonNull(config);

        final ModuleSystemBuilder builder = config.moduleSystemBuilder;

        builder.export(SqlDB.class, module -> {
            module.export(new SqlDBImpl(
                module.require(BaseSqlDB.class),
                module.require(SqlBuilderUtils.class),
                module.require(DbInterceptors.class)
            ));
        });

        exportBaseSqlDb(config);

        builder.export(SqlBuilderUtils.class, module -> {
            module.export(new SqlBuilderUtilsImpl());
        });

        builder.export(DbInterceptors.class, module -> {
            module.export(new DbInterceptorsImpl(ImmutableList.of(), ImmutableList.of()));
        });

        return builder;
    }

    static ModuleSystemBuilder exportBaseSqlDb(ExportToParams config) {
        final ModuleSystemBuilder builder = config.moduleSystemBuilder;
        final JDBCClient jdbcClient = config.jdbcClient;

        builder.export(BaseSqlDB.class, module -> {
            module.export(new BaseSqlDBImpl(
                module.require(SqlExecutor.class),
                module.require(SqlQueryBuilderUtils.class)
            ));
        });

        builder.export(SqlExecutor.class, module -> {
            module.export(new SqlExecutorImpl(
                jdbcClient
            ));
        });

        exportSqlQueryBuilderUtils(builder);

        return builder;
    }

    static void exportSqlQueryBuilderUtils(ModuleSystemBuilder builder) {

        builder.export(SqlQueryBuilderUtils.class, module -> {
            module.export(new SqlQueryBuilderUtilsImpl(
                module.require(SqlBuilderDialect.class)
            ));
        });

        builder.export(SqlBuilderDialect.class, module -> {
            module.export(new MySqlSqlBuilderDialectImpl());
        });
    }

    @Value
    @Builder
    final class ExportToParams {
        final ModuleSystemBuilder moduleSystemBuilder;
        final JDBCClient jdbcClient;

        public ExportToParams(ModuleSystemBuilder moduleSystemBuilder, JDBCClient jdbcClient) {
            Objects.requireNonNull(moduleSystemBuilder);
            Objects.requireNonNull(jdbcClient);
            this.moduleSystemBuilder = moduleSystemBuilder;
            this.jdbcClient = jdbcClient;
        }
    }
}
