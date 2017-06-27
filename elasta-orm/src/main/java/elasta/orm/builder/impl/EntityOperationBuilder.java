package elasta.orm.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.orm.delete.*;
import elasta.orm.delete.builder.DeleteFunctionBuilder;
import elasta.orm.delete.builder.DeleteTableFunctionBuilder;
import elasta.orm.delete.builder.ListTablesToDeleteFunctionBuilder;
import elasta.orm.delete.builder.impl.DeleteFunctionBuilderImpl;
import elasta.orm.delete.builder.impl.DeleteTableFunctionBuilderImpl;
import elasta.orm.delete.builder.impl.ListTablesToDeleteFunctionBuilderImpl;
import elasta.orm.delete.impl.TableToDeleteTableFunctionMapImpl;
import elasta.orm.delete.loader.DependencyDataLoaderGraph;
import elasta.orm.delete.loader.impl.DependencyDataLoaderBuilderImpl;
import elasta.orm.delete.loader.impl.DependencyDataLoaderGraphBuilderImpl;
import elasta.orm.delete.loader.impl.TableToTableDependenciesMapBuilderImpl;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Entity;
import elasta.orm.event.builder.BuilderContext;
import elasta.orm.event.builder.impl.BuilderContextImpl;
import elasta.orm.impl.BaseOrmImpl;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;
import elasta.orm.relation.delete.builder.DeleteChildRelationsFunctionBuilder;
import elasta.orm.relation.delete.builder.impl.DeleteChildRelationsFunctionBuilderImpl;
import elasta.orm.upsert.IdGenerator;
import elasta.orm.upsert.UpsertFunction;
import elasta.orm.upsert.builder.UpsertFunctionBuilder;
import elasta.orm.upsert.builder.impl.UpsertFunctionBuilderImpl;
import elasta.sql.SqlDB;

import java.util.*;

/**
 * Created by sohan on 4/12/2017.
 */
final class EntityOperationBuilder {
    final EntityMappingHelper helper;
    final SqlDB sqlDB;
    final UpsertFunctionBuilder upsertFunctionBuilder;
    final DeleteFunctionBuilder deleteFunctionBuilder;
    final DeleteChildRelationsFunctionBuilder deleteChildRelationsFunctionBuilder;
    final Map<String, Optional<DeleteTableFunction>> deleteTableFunctionMap;
    final Map<String, Optional<ListTablesToDeleteFunction>> listTablesToDeleteFunctionMap;
    final Map<String, Optional<UpsertFunction>> upsertFunctionMap;
    final Map<String, Optional<DeleteChildRelationsFunction>> deleteChildRelationsFunctionMap;
    final BuilderContext<DeleteTableFunction> deleteTableFunctionBuilderContext;
    final BuilderContext<ListTablesToDeleteFunction> listTablesToDeleteFunctionBuilderContext;
    final BuilderContext<UpsertFunction> upsertFunctionBuilderContext;
    final BuilderContext<DeleteChildRelationsFunction> deleteChildRelationsFunctionBuilderContext;
    final IdGenerator idGenerator;

    EntityOperationBuilder(EntityMappingHelper helper, SqlDB sqlDB, IdGenerator idGenerator) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(sqlDB);
        Objects.requireNonNull(idGenerator);
        this.helper = helper;
        this.sqlDB = sqlDB;
        this.idGenerator = idGenerator;

        this.deleteTableFunctionBuilderContext = new BuilderContextImpl<>(
            this.deleteTableFunctionMap = new LinkedHashMap<>()
        );

        this.listTablesToDeleteFunctionBuilderContext = new BuilderContextImpl<>(
            this.listTablesToDeleteFunctionMap = new LinkedHashMap<>()
        );

        this.upsertFunctionBuilderContext = new BuilderContextImpl<>(
            this.upsertFunctionMap = new LinkedHashMap<>()
        );

        this.deleteChildRelationsFunctionBuilderContext = new BuilderContextImpl<>(
            this.deleteChildRelationsFunctionMap = new LinkedHashMap<>()
        );

        upsertFunctionBuilder = new UpsertFunctionBuilderImpl(
            this.helper,
            this.idGenerator
        );
        this.deleteFunctionBuilder = deleteFunctionBuilder();

        this.deleteChildRelationsFunctionBuilder = new DeleteChildRelationsFunctionBuilderImpl(
            helper
        );
    }

    private DeleteFunctionBuilder deleteFunctionBuilder() {

        TableToTableDependenciesMap tableToTableDependenciesMap = tableToTableDependenciesMap();

        return new DeleteFunctionBuilderImpl(
            helper,
            listTablesToDeleteFunctionBuilder(),
            tableToTableDependenciesMap,
            tableToDeleteTableFunctionMap(tableToTableDependenciesMap),
            dependencyDataLoaderGraph(tableToTableDependenciesMap),
            sqlDB
        );
    }

    private TableToDeleteTableFunctionMap tableToDeleteTableFunctionMap(TableToTableDependenciesMap tableToTableDependenciesMap) {
        DeleteTableFunctionBuilder deleteTableFunctionBuilder = deleteTableFunctionBuilder();
        ImmutableMap.Builder<String, DeleteTableFunction> mapBuilder = ImmutableMap.builder();
        List<String> tables = helper.getTables();
        tables.forEach(table -> {
            mapBuilder.put(table, deleteTableFunctionBuilder.build(table, deleteTableFunctionBuilderContext, tableToTableDependenciesMap));
        });
        return new TableToDeleteTableFunctionMapImpl(
            mapBuilder.build()
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


    OperationMapBuilder.EntityAndEntityOperation toEntityOperation(Entity entity) {
        return new OperationMapBuilder.EntityAndEntityOperation(
            entity.getName(),
            entityOperation(entity)
        );
    }

    private BaseOrmImpl.EntityOperation entityOperation(Entity entity) {
        final String entityName = entity.getName();
        return new BaseOrmImpl.EntityOperation(
            upsertFunction(entityName),
            deleteFunction(entityName),
            deleteChildRelationsFunction(entityName)
        );
    }

    private DeleteChildRelationsFunction deleteChildRelationsFunction(String entityName) {
        return deleteChildRelationsFunctionBuilder.build(entityName, deleteChildRelationsFunctionBuilderContext);
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
        return upsertFunctionBuilder.build(entity, upsertFunctionBuilderContext);
    }

    public void makeAllBuilderContextsImmutable() {

        deleteTableFunctionBuilderContext.makeImmutable();
        listTablesToDeleteFunctionBuilderContext.makeImmutable();
        upsertFunctionBuilderContext.makeImmutable();
        deleteChildRelationsFunctionBuilderContext.makeImmutable();
    }
}
