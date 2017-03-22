package elasta.orm.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.orm.BaseOrm;
import elasta.orm.builder.BaseOrmBuilder;
import elasta.orm.delete.DeleteFunction;
import elasta.orm.delete.DeleteTableFunction;
import elasta.orm.delete.ListTablesToDeleteFunction;
import elasta.orm.delete.TableToTableDependenciesMap;
import elasta.orm.delete.builder.*;
import elasta.orm.delete.builder.impl.*;
import elasta.orm.delete.loader.DependencyDataLoaderGraph;
import elasta.orm.delete.loader.impl.DependencyDataLoaderBuilderImpl;
import elasta.orm.delete.loader.impl.DependencyDataLoaderGraphBuilderImpl;
import elasta.orm.delete.loader.impl.TableToTableDependenciesMapBuilderImpl;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Entity;
import elasta.orm.impl.BaseOrmImpl;
import elasta.orm.query.QueryExecutor;
import elasta.orm.query.read.builder.ObjectReaderBuilder;
import elasta.orm.upsert.UpsertFunction;
import elasta.orm.upsert.builder.FunctionMap;
import elasta.orm.upsert.builder.FunctionMapImpl;
import elasta.orm.upsert.builder.UpsertFunctionBuilder;
import elasta.orm.upsert.builder.impl.UpsertFunctionBuilderImpl;
import elasta.sql.SqlDB;
import lombok.Value;

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
            operationMap(entities),
            queryExecutor,
            sqlDB
        );
    }

    private Map<String, BaseOrmImpl.EntityOperation> operationMap(Collection<Entity> entities) {
        ImmutableMap.Builder<String, BaseOrmImpl.EntityOperation> mapBuilder = ImmutableMap.builder();
        InternalEntityOperationBuilder operationBuilder = new InternalEntityOperationBuilder();
        entities.stream()
            .map(operationBuilder::toEntityOperation)
            .forEach(
                entityAndEntityOperation -> mapBuilder
                    .put(entityAndEntityOperation.getEntity(), entityAndEntityOperation.getEntityOperation())
            );
        return mapBuilder.build();
    }

    @Value
    private static final class EntityAndEntityOperation {
        final String entity;
        final BaseOrmImpl.EntityOperation entityOperation;

        public EntityAndEntityOperation(String entity, BaseOrmImpl.EntityOperation entityOperation) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(entityOperation);
            this.entity = entity;
            this.entityOperation = entityOperation;
        }
    }

    private final class InternalEntityOperationBuilder {
        final UpsertFunctionBuilder upsertFunctionBuilder;
        final DeleteFunctionBuilder deleteFunctionBuilder;
        final Map<String, Optional<DeleteTableFunction>> deleteTableFunctionMap;
        final Map<String, Optional<ListTablesToDeleteFunction>> listTablesToDeleteFunctionMap;
        final Map<String, Optional<UpsertFunction>> upsertFunctionMap;
        final DeleteTableFunctionBuilderContext deleteTableFunctionBuilderContext;
        final ListTablesToDeleteFunctionBuilderContext listTablesToDeleteFunctionBuilderContext;
        final FunctionMap<UpsertFunction> upsertFunctionFunctionMap;

        public InternalEntityOperationBuilder() {
            upsertFunctionBuilder = new UpsertFunctionBuilderImpl(
                helper
            );
            this.deleteFunctionBuilder = deleteFunctionBuilder();
            this.deleteTableFunctionMap = new LinkedHashMap<>();
            this.deleteTableFunctionBuilderContext = new DeleteTableFunctionBuilderContextImpl(
                deleteTableFunctionMap
            );
            this.listTablesToDeleteFunctionMap = new LinkedHashMap<>();
            this.listTablesToDeleteFunctionBuilderContext = new ListTablesToDeleteFunctionBuilderContextImpl(
                listTablesToDeleteFunctionMap
            );
            this.upsertFunctionMap = new LinkedHashMap<>();
            this.upsertFunctionFunctionMap = new FunctionMapImpl<>(
                upsertFunctionMap
            );
        }

        private DeleteFunctionBuilder deleteFunctionBuilder() {

            TableToTableDependenciesMap tableToTableDependenciesMap = tableToTableDependenciesMap();

            return new DeleteFunctionBuilderImpl(
                helper,
                deleteTableFunctionBuilder(),
                listTablesToDeleteFunctionBuilder(),
                tableToTableDependenciesMap,
                dependencyDataLoaderGraph(tableToTableDependenciesMap),
                sqlDB
            );
        }

        private DependencyDataLoaderGraph dependencyDataLoaderGraph(TableToTableDependenciesMap tableToTableDependenciesMap) {
            return new DependencyDataLoaderGraphBuilderImpl(
                new DependencyDataLoaderBuilderImpl(
                    helper, sqlDB
                )
            ).build(tableToTableDependenciesMap);
        }

        private TableToTableDependenciesMap tableToTableDependenciesMap() {
            return new TableToTableDependenciesMapBuilderImpl(
                helper
            ).build();
        }

        private ListTablesToDeleteFunctionBuilder listTablesToDeleteFunctionBuilder() {
            return new ListTablesToDeleteFunctionBuilderImpl(
                helper
            );
        }

        private DeleteTableFunctionBuilder deleteTableFunctionBuilder() {
            return new DeleteTableFunctionBuilderImpl(
                helper
            );
        }


        EntityAndEntityOperation toEntityOperation(Entity entity) {
            return new EntityAndEntityOperation(
                entity.getName(),
                entityOperation(entity)
            );
        }

        private BaseOrmImpl.EntityOperation entityOperation(Entity entity) {
            final String entityName = entity.getName();
            return new BaseOrmImpl.EntityOperation(
                upsertFunction(entityName),
                deleteFunction(entityName)
            );
        }

        private DeleteFunction deleteFunction(String entity) {
            return deleteFunctionBuilder.build(
                DeleteFunctionBuilder.Params.builder()
                    .deleteTableFunctionBuilderContext(
                        deleteTableFunctionBuilderContext
                    )
                    .entity(entity)
                    .listTablesToDeleteFunctionBuilderContext(
                        listTablesToDeleteFunctionBuilderContext
                    )
                    .build()
            );
        }

        private UpsertFunction upsertFunction(String entity) {
            return upsertFunctionBuilder.build(entity, upsertFunctionFunctionMap);
        }
    }
}
