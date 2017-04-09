package elasta.orm.delete.builder.impl;

import elasta.orm.delete.*;
import elasta.orm.delete.builder.*;
import elasta.orm.delete.impl.DeleteFunctionImpl;
import elasta.orm.delete.impl.ReloadTableDataFunctionImpl;
import elasta.orm.delete.loader.DependencyDataLoaderGraph;
import elasta.orm.delete.loader.MultiTableDependenciesLoader;
import elasta.orm.delete.loader.MultiTableDependenciesLoaderImpl;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.event.builder.BuilderContext;
import elasta.sql.SqlDB;

import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
final public class DeleteFunctionBuilderImpl implements DeleteFunctionBuilder {
    final EntityMappingHelper helper;
    final ListTablesToDeleteFunctionBuilder listTablesToDeleteFunctionBuilder;
    final TableToTableDependenciesMap tableToTableDependenciesMap;
    final TableToDeleteTableFunctionMap tableToDeleteTableFunctionMap;
    final DependencyDataLoaderGraph dependencyDataLoaderGraph;
    final SqlDB sqlDB;

    public DeleteFunctionBuilderImpl(EntityMappingHelper helper, ListTablesToDeleteFunctionBuilder listTablesToDeleteFunctionBuilder, TableToTableDependenciesMap tableToTableDependenciesMap, TableToDeleteTableFunctionMap tableToDeleteTableFunctionMap, DependencyDataLoaderGraph dependencyDataLoaderGraph, SqlDB sqlDB) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(listTablesToDeleteFunctionBuilder);
        Objects.requireNonNull(tableToTableDependenciesMap);
        Objects.requireNonNull(tableToDeleteTableFunctionMap);
        Objects.requireNonNull(dependencyDataLoaderGraph);
        Objects.requireNonNull(sqlDB);
        this.helper = helper;
        this.listTablesToDeleteFunctionBuilder = listTablesToDeleteFunctionBuilder;
        this.tableToTableDependenciesMap = tableToTableDependenciesMap;
        this.tableToDeleteTableFunctionMap = tableToDeleteTableFunctionMap;
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
            tableToDeleteTableFunctionMap
        );
    }

    private ReloadTableDataFunction reloadTableDataFunction() {
        return new ReloadTableDataFunctionImpl(
            helper, tableToTableDependenciesMap,
            sqlDB
        );
    }

    private MultiTableDependenciesLoader multiTableDependenciesLoader() {
        return new MultiTableDependenciesLoaderImpl(
            dependencyDataLoaderGraph
        );
    }

    private ListTablesToDeleteFunction listTablesToDeleteFunction(String entity, BuilderContext<ListTablesToDeleteFunction> listTablesToDeleteFunctionBuilderContext) {
        return listTablesToDeleteFunctionBuilder.build(
            entity,
            listTablesToDeleteFunctionBuilderContext
        );
    }
}
