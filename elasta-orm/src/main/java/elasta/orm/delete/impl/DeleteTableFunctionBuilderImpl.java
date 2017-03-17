package elasta.orm.delete.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.delete.*;
import elasta.orm.entitymodel.EntityMappingHelper;
import elasta.orm.entitymodel.columnmapping.DbColumnMapping;
import elasta.orm.entitymodel.columnmapping.IndirectDbColumnMapping;
import elasta.orm.entitymodel.ForeignColumnMapping;
import elasta.orm.entitymodel.columnmapping.DirectDbColumnMapping;
import elasta.orm.upsert.ColumnToColumnMapping;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/11/2017.
 */
final public class DeleteTableFunctionBuilderImpl implements DeleteTableFunctionBuilder {
    final EntityMappingHelper helper;

    public DeleteTableFunctionBuilderImpl(EntityMappingHelper helper) {
        Objects.requireNonNull(helper);
        this.helper = helper;
    }

    @Override
    public DeleteTableFunction build(String table, DeleteTableFunctionBuilderContext context, TableToTableDependenciesMap tableToTableDependenciesMap) {

        if (context.contains(table)) {
            return context.get(table);
        }

        context.putEmpty(table);

        final DeleteTableFunction deleteTableFunction = buildDeleteFunction(table, context, tableToTableDependenciesMap);

        context.put(table, deleteTableFunction);

        return deleteTableFunction;
    }

    private DeleteTableFunction buildDeleteFunction(String table, DeleteTableFunctionBuilderContext context, TableToTableDependenciesMap tableToTableDependenciesMap) {

        ImmutableList.Builder<IndirectDependencyDeleteHandler> indirectListBuilder = ImmutableList.builder();
        ImmutableList.Builder<DirectDependencyDeleteHandler> directListBuilder = ImmutableList.builder();

        tableToTableDependenciesMap.get(table)
            .forEach(dependencyInfo -> {
                DbColumnMapping dbColumnMapping = dependencyInfo.getDbColumnMapping();
                switch (dbColumnMapping.getColumnType()) {
                    case DIRECT: {
                        DirectDbColumnMapping mapping = (DirectDbColumnMapping) dbColumnMapping;
                        directListBuilder.add(
                            new DirectDependencyDeleteHandlerImpl(
                                dependencyInfo.getDependentTable(),
                                columnToColumnMappingsDirect(mapping),
                                createDeleteFunction(dependencyInfo.getDependentTable(), context, tableToTableDependenciesMap)
                            )
                        );
                    }
                    break;
                    case INDIRECT: {
                        IndirectDbColumnMapping mapping = (IndirectDbColumnMapping) dbColumnMapping;
                        indirectListBuilder.add(
                            new IndirectDependencyDeleteHandlerImpl(
                                mapping.getRelationTable(),
                                columnToColumnMappingsIndirect(mapping.getDstForeignColumnMappingList())
                            )
                        );
                    }
                    break;
                }
            });

        ImmutableList<DirectDependencyDeleteHandler> directDependencyDeleteHandlers = directListBuilder.build();
        ImmutableList<IndirectDependencyDeleteHandler> indirectDependencyDeleteHandlers = indirectListBuilder.build();

        return new DeleteTableFunctionImpl(
            directDependencyDeleteHandlers.toArray(new DirectDependencyDeleteHandler[directDependencyDeleteHandlers.size()]),
            indirectDependencyDeleteHandlers.toArray(new IndirectDependencyDeleteHandler[indirectDependencyDeleteHandlers.size()])
        );
    }

    private DeleteTableFunction createDeleteFunction(String dependentTable, DeleteTableFunctionBuilderContext context, TableToTableDependenciesMap tableToTableDependenciesMap) {

        if (context.contains(dependentTable)) {
            return context.get(dependentTable);
        }

        if (context.isEmpty(dependentTable)) {
            return new ProxyDeleteTableFunction(dependentTable, context);
        }

        return new DeleteTableFunctionBuilderImpl(
            helper
        ).build(dependentTable, context, tableToTableDependenciesMap);
    }

    private ColumnToColumnMapping[] columnToColumnMappingsDirect(DirectDbColumnMapping mapping) {

        List<ForeignColumnMapping> foreignColumnMappingList = mapping.getForeignColumnMappingList();

        ColumnToColumnMapping[] columnToColumnMappings = new ColumnToColumnMapping[foreignColumnMappingList.size()];

        for (int i = 0; i < foreignColumnMappingList.size(); i++) {
            ForeignColumnMapping foreignColumnMapping = foreignColumnMappingList.get(i);
            columnToColumnMappings[i] = new ColumnToColumnMapping(
                foreignColumnMapping.getSrcColumn().getName(),
                foreignColumnMapping.getDstColumn().getName()
            );
        }
        return columnToColumnMappings;
    }

    private ColumnToColumnMapping[] columnToColumnMappingsIndirect(List<ForeignColumnMapping> foreignColumnMappings) {
        ColumnToColumnMapping[] columnToColumnMappings = new ColumnToColumnMapping[foreignColumnMappings.size()];
        for (int i = 0; i < columnToColumnMappings.length; i++) {
            ForeignColumnMapping foreignColumnMapping = foreignColumnMappings.get(i);
            columnToColumnMappings[i] = new ColumnToColumnMapping(
                foreignColumnMapping.getSrcColumn().getName(),
                foreignColumnMapping.getDstColumn().getName()
            );
        }
        return columnToColumnMappings;
    }
}
