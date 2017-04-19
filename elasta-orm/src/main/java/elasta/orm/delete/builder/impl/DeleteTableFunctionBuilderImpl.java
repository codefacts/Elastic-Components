package elasta.orm.delete.builder.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.orm.delete.*;
import elasta.orm.delete.builder.DeleteTableFunctionBuilder;
import elasta.orm.delete.impl.*;
import elasta.orm.delete.loader.impl.DependencyInfo;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.columnmapping.IndirectRelationMapping;
import elasta.orm.entity.core.ForeignColumnMapping;
import elasta.orm.entity.core.columnmapping.DirectRelationMapping;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.event.builder.BuilderContext;
import elasta.sql.core.ColumnToColumnMapping;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    public DeleteTableFunction build(String table, BuilderContext<DeleteTableFunction> context, TableToTableDependenciesMap tableToTableDependenciesMap) {

        requireNonNulls(table, context, tableToTableDependenciesMap);

        if (context.contains(table)) {
            return context.get(table);
        }

        if (context.isEmpty(table)) {
            return new ProxyDeleteTableFunction(table, context);
        }

        context.putEmpty(table);

        final DeleteTableFunction deleteTableFunction = buildDeleteFunction(table, context, tableToTableDependenciesMap);

        context.put(table, deleteTableFunction);

        return deleteTableFunction;
    }

    private void requireNonNulls(String table, BuilderContext<DeleteTableFunction> context, TableToTableDependenciesMap tableToTableDependenciesMap) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(context);
        Objects.requireNonNull(tableToTableDependenciesMap);
    }

    private DeleteTableFunction buildDeleteFunction(String table, BuilderContext<DeleteTableFunction> context, TableToTableDependenciesMap tableToTableDependenciesMap) {

        ImmutableSet.Builder<IndirectDependencyDeleteHandler> indirectListBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<DirectDependencyDeleteHandler> directListBuilder = ImmutableSet.builder();
        ImmutableSet.Builder<DirectRelationDeleteHandler> relationDeleteHandlersBuilder = ImmutableSet.builder();

        DeleteUtils.getTableDependenciesForLoadAndDelete(tableToTableDependenciesMap.get(table))
            .forEach(dependencyInfo -> {
                RelationMapping relationMapping = dependencyInfo.getRelationMapping();
                switch (relationMapping.getColumnType()) {
                    case DIRECT: {
                        DirectRelationMapping mapping = (DirectRelationMapping) relationMapping;
                        switch (mapping.getOptions().getLoadAndDeleteParent()) {
                            case LOAD_AND_DELETE: {
                                directListBuilder.add(
                                    directDependencyDeleteHandler(mapping, dependencyInfo, context, tableToTableDependenciesMap)
                                );
                            }
                            break;
                            case SET_TO_NULL: {
                                relationDeleteHandlersBuilder.add(
                                    relationDeleteHandler(dependencyInfo.getDependentTable(), mapping.getForeignColumnMappingList())
                                );
                            }
                            break;
                        }
                    }
                    break;
                    case INDIRECT: {
                        indirectListBuilder.add(
                            indirectDependencyDeleteHandler((IndirectRelationMapping) relationMapping)
                        );
                    }
                    break;
                }
            });

        Arrays.stream(helper.getDbMappingByTable(table).getRelationMappings()).forEach(relationMapping -> {
            switch (relationMapping.getColumnType()) {
                case INDIRECT: {
                    IndirectRelationMapping mapping = (IndirectRelationMapping) relationMapping;
                    indirectListBuilder.add(
                        new IndirectDependencyDeleteHandlerImpl(
                            mapping.getRelationTable(),
                            columnToColumnMappingsIndirect(mapping.getSrcForeignColumnMappingList())
                        )
                    );
                }
            }
        });

        Set<DirectRelationDeleteHandler> relationDeleteHandlers = relationDeleteHandlersBuilder.build();
        Set<DirectDependencyDeleteHandler> directDependencyDeleteHandlers = directListBuilder.build();
        Set<IndirectDependencyDeleteHandler> indirectDependencyDeleteHandlers = indirectListBuilder.build();

        return new DeleteTableFunctionImpl(
            relationDeleteHandlers.toArray(new DirectRelationDeleteHandler[relationDeleteHandlers.size()]),
            directDependencyDeleteHandlers.toArray(new DirectDependencyDeleteHandler[directDependencyDeleteHandlers.size()]),
            indirectDependencyDeleteHandlers.toArray(new IndirectDependencyDeleteHandler[indirectDependencyDeleteHandlers.size()])
        );
    }

    private DirectRelationDeleteHandler relationDeleteHandler(String dependentTable, List<ForeignColumnMapping> foreignColumnMappingList) {

        DeleteData.ColumnAndOptionalValuePair[] columnAndOptionalValuePairs = new DeleteData.ColumnAndOptionalValuePair[foreignColumnMappingList.size()];
        ImmutableList.Builder<String> childTableColumnsBuilder = ImmutableList.builder();

        for (int i = 0; i < foreignColumnMappingList.size(); i++) {
            ForeignColumnMapping foreignColumnMapping = foreignColumnMappingList.get(i);

            columnAndOptionalValuePairs[i] = new DeleteData.ColumnAndOptionalValuePair(
                foreignColumnMapping.getSrcColumn()
            );

            childTableColumnsBuilder.add(foreignColumnMapping.getDstColumn());
        }

        return new DirectRelationDeleteHandlerImpl(
            dependentTable,
            columnAndOptionalValuePairs,
            childTableColumnsBuilder.build()
        );
    }

    private DirectDependencyDeleteHandler directDependencyDeleteHandler(DirectRelationMapping mapping, DependencyInfo dependencyInfo, BuilderContext<DeleteTableFunction> context, TableToTableDependenciesMap tableToTableDependenciesMap) {
        return new DirectDependencyDeleteHandlerImpl(
            dependencyInfo.getDependentTable(),
            columnToColumnMappingsDirect(mapping),
            createDeleteFunction(dependencyInfo.getDependentTable(), context, tableToTableDependenciesMap)
        );
    }

    private IndirectDependencyDeleteHandler indirectDependencyDeleteHandler(IndirectRelationMapping mapping) {
        return new IndirectDependencyDeleteHandlerImpl(
            mapping.getRelationTable(),
            columnToColumnMappingsIndirect(mapping.getDstForeignColumnMappingList())
        );
    }

    private DeleteTableFunction createDeleteFunction(String dependentTable, BuilderContext<DeleteTableFunction> context, TableToTableDependenciesMap tableToTableDependenciesMap) {

        return build(dependentTable, context, tableToTableDependenciesMap);
    }

    private ColumnToColumnMapping[] columnToColumnMappingsDirect(DirectRelationMapping mapping) {

        List<ForeignColumnMapping> foreignColumnMappingList = mapping.getForeignColumnMappingList();

        ColumnToColumnMapping[] columnToColumnMappings = new ColumnToColumnMapping[foreignColumnMappingList.size()];

        for (int i = 0; i < foreignColumnMappingList.size(); i++) {
            ForeignColumnMapping foreignColumnMapping = foreignColumnMappingList.get(i);
            columnToColumnMappings[i] = new ColumnToColumnMapping(
                foreignColumnMapping.getSrcColumn(),
                foreignColumnMapping.getDstColumn()
            );
        }
        return columnToColumnMappings;
    }

    private ColumnToColumnMapping[] columnToColumnMappingsIndirect(List<ForeignColumnMapping> foreignColumnMappings) {
        ColumnToColumnMapping[] columnToColumnMappings = new ColumnToColumnMapping[foreignColumnMappings.size()];
        for (int i = 0; i < columnToColumnMappings.length; i++) {
            ForeignColumnMapping foreignColumnMapping = foreignColumnMappings.get(i);
            columnToColumnMappings[i] = new ColumnToColumnMapping(
                foreignColumnMapping.getSrcColumn(),
                foreignColumnMapping.getDstColumn()
            );
        }
        return columnToColumnMappings;
    }
}
