package elasta.orm.delete.builder.impl;

import elasta.orm.delete.*;
import elasta.orm.delete.builder.*;
import elasta.orm.delete.impl.DeleteFunctionImpl;
import elasta.orm.delete.impl.ReloadTableDataFunctionImpl;
import elasta.orm.delete.loader.DependencyDataLoaderGraph;
import elasta.orm.delete.loader.MultiTableDependenciesLoader;
import elasta.orm.delete.loader.MultiTableDependenciesLoaderImpl;
import elasta.orm.entity.EntityMappingHelper;
import elasta.sql.SqlDB;

import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
final public class DeleteFunctionBuilderImpl implements DeleteFunctionBuilder {
    final EntityMappingHelper helper;
    final DeleteTableFunctionBuilder deleteTableFunctionBuilder;
    final ListTablesToDeleteFunctionBuilder listTablesToDeleteFunctionBuilder;
    final TableToTableDependenciesMap tableToTableDependenciesMap;
    final DependencyDataLoaderGraph dependencyDataLoaderGraph;
    final SqlDB sqlDB;

    public DeleteFunctionBuilderImpl(EntityMappingHelper helper, DeleteTableFunctionBuilder deleteTableFunctionBuilder, ListTablesToDeleteFunctionBuilder listTablesToDeleteFunctionBuilder, TableToTableDependenciesMap tableToTableDependenciesMap, DependencyDataLoaderGraph dependencyDataLoaderGraph, SqlDB sqlDB) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(deleteTableFunctionBuilder);
        Objects.requireNonNull(listTablesToDeleteFunctionBuilder);
        Objects.requireNonNull(tableToTableDependenciesMap);
        Objects.requireNonNull(dependencyDataLoaderGraph);
        Objects.requireNonNull(sqlDB);
        this.helper = helper;
        this.deleteTableFunctionBuilder = deleteTableFunctionBuilder;
        this.listTablesToDeleteFunctionBuilder = listTablesToDeleteFunctionBuilder;
        this.tableToTableDependenciesMap = tableToTableDependenciesMap;
        this.dependencyDataLoaderGraph = dependencyDataLoaderGraph;
        this.sqlDB = sqlDB;
    }

    @Override
    public DeleteFunction build(Params params) {

        final String entity = params.getEntity();

        return new DeleteFunctionImpl(
            listTablesToDeleteFunction(
                entity,
                params.getListTablesToDeleteFunctionBuilderContext()
            ),
            multiTableDependenciesLoader(),
            reloadTableDataFunction(),
            deleteTableFunction(
                helper.getTable(entity),
                params.getDeleteTableFunctionBuilderContext()
            )
        );
    }

    private DeleteTableFunction deleteTableFunction(String table, DeleteTableFunctionBuilderContext deleteTableFunctionBuilderContext) {

        return deleteTableFunctionBuilder
            .build(
                table,
                deleteTableFunctionBuilderContext,
                tableToTableDependenciesMap
            );
    }

    private ReloadTableDataFunction reloadTableDataFunction() {
        return new ReloadTableDataFunctionImpl(
            tableToTableDependenciesMap,
            sqlDB
        );
    }

    private MultiTableDependenciesLoader multiTableDependenciesLoader() {
        return new MultiTableDependenciesLoaderImpl(
            dependencyDataLoaderGraph
        );
    }

    private ListTablesToDeleteFunction listTablesToDeleteFunction(String entity, ListTablesToDeleteFunctionBuilderContext listTablesToDeleteFunctionBuilderContext) {
        return listTablesToDeleteFunctionBuilder.build(
            entity,
            listTablesToDeleteFunctionBuilderContext
        );
    }
}
