package elasta.orm;

import elasta.criteria.json.mapping.*;
import elasta.criteria.json.mapping.impl.*;
import elasta.module.ModuleExporter;
import elasta.module.ModuleSystemBuilder;
import elasta.orm.builder.impl.OperationMapBuilder;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.EntityUtils;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.impl.EntityMappingHelperImpl;
import elasta.sql.*;
import elasta.orm.impl.BaseOrmImpl;
import elasta.orm.impl.OrmImpl;
import elasta.orm.impl.QueryDataLoaderImpl;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.iml.QueryExecutorImpl;
import io.vertx.ext.jdbc.JDBCClient;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Jango on 9/14/2016.
 */
public interface OrmExporter extends ModuleExporter {

    static ModuleSystemBuilder exportTo(ExportToParams params) {
        Objects.requireNonNull(params);

        final JDBCClient jdbcClient = params.getJdbcClient();
        final ModuleSystemBuilder builder = params.getModuleSystemBuilder();
        final Collection<Entity> entities = params.getEntities();

        builder.export(Orm.class, module -> {
            module.export(
                new OrmImpl(
                    module.require(EntityMappingHelper.class),
                    module.require(BaseOrm.class),
                    module.require(QueryDataLoader.class)
                )
            );
        });

        builder.export(EntityMappingHelper.class, module -> {
            module.export(new EntityMappingHelperImpl(
                EntityUtils.toEntityNameToEntityMap(entities)
            ));
        });

        exportBaseOrm(params);

        builder.export(QueryDataLoader.class, module -> {
            module.export(
                new QueryDataLoaderImpl(
                    module.require(BaseOrm.class),
                    module.require(EntityMappingHelper.class)
                )
            );
        });

        SqlExporter.exportTo(
            SqlExporter.ExportToParams.builder()
                .jdbcClient(jdbcClient)
                .moduleSystemBuilder(builder)
                .build()
        );

        return builder;
    }

    static ModuleSystemBuilder exportBaseOrm(ExportToParams params) {

        final ModuleSystemBuilder builder = params.getModuleSystemBuilder();
        final JDBCClient jdbcClient = params.getJdbcClient();
        final Collection<Entity> entities = params.getEntities();
        final String isNewKey = params.getIsNewKey();

        builder.export(BaseOrm.class, module -> {

            final Map<String, BaseOrmImpl.EntityOperation> operationMap = new OperationMapBuilder(
                entities,
                module.require(EntityMappingHelper.class),
                module.require(SqlDB.class),
                isNewKey
            ).build();

            module.export(new BaseOrmImpl(
                operationMap,
                module.require(QueryExecutor.class)
            ));
        });

        builder.export(QueryExecutor.class, module -> {
            module.export(new QueryExecutorImpl(
                module.require(EntityMappingHelper.class),
                module.require(JsonToFuncConverterMap.class),
                module.require(GenericJsonToFuncConverter.class),
                module.require(SqlDB.class)
            ));
        });

        exportJsonToFuncConverterMap(builder);

        exportGenericJsonToFuncConverter(builder);

        return builder;
    }

    static void exportGenericJsonToFuncConverter(ModuleSystemBuilder builder) {
        builder.export(GenericJsonToFuncConverter.class, module -> {
            module.export(
                new GenericJsonToFuncConverterImpl()
            );
        });
    }

    static void exportJsonToFuncConverterMap(ModuleSystemBuilder builder) {

        builder.export(JsonToFuncConverterMap.class, module -> {

            module.export(
                new JsonToFuncConverterMapBuilderImpl(
                    module.require(JsonToFuncConverterHelper.class),
                    module.require(JsonToFuncConverterBuilderHelper.class)
                ).build()
            );

        });

        builder.export(JsonToFuncConverterHelper.class, module -> {
            module.export(new JsonToFuncConverterHelperImpl(
                module.require(JsonToFuncConverterBuilderHelper.class)
            ));
        });

        exportJsonToFuncConverterBuilderHelper(builder);
    }

    static void exportJsonToFuncConverterBuilderHelper(ModuleSystemBuilder builder) {

        builder.export(JsonToFuncConverterBuilderHelper.class, module -> {
            module.export(
                new JsonToFuncConverterBuilderHelperImpl(
                    module.require(ValueHolderOperationBuilderHelper.class)
                )
            );
        });

        builder.export(ValueHolderOperationBuilderHelper.class, module -> {
            module.export(new ValueHolderOperationBuilderHelperImpl());
        });
    }

    @Value
    @Builder
    final class ExportToParams {
        final ModuleSystemBuilder moduleSystemBuilder;
        final JDBCClient jdbcClient;
        final Collection<Entity> entities;
        final String isNewKey;

        public ExportToParams(ModuleSystemBuilder moduleSystemBuilder, JDBCClient jdbcClient, Collection<Entity> entities, String isNewKey) {
            Objects.requireNonNull(moduleSystemBuilder);
            Objects.requireNonNull(jdbcClient);
            Objects.requireNonNull(entities);
            Objects.requireNonNull(isNewKey);
            this.moduleSystemBuilder = moduleSystemBuilder;
            this.jdbcClient = jdbcClient;
            this.entities = entities;
            this.isNewKey = isNewKey;
        }
    }
}
