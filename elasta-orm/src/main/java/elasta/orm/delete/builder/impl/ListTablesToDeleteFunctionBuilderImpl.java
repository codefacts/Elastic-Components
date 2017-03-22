package elasta.orm.delete.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.delete.*;
import elasta.orm.delete.builder.ListTablesToDeleteFunctionBuilder;
import elasta.orm.delete.builder.ListTablesToDeleteFunctionBuilderContext;
import elasta.orm.delete.impl.DirectChildHandlerImpl;
import elasta.orm.delete.impl.IndirectChildHandlerImpl;
import elasta.orm.delete.impl.ListTablesToDeleteFunctionImpl;
import elasta.orm.delete.impl.VirtualChildHandlerImpl;
import elasta.orm.entity.core.ColumnType;import elasta.orm.entity.core.DbMapping;import elasta.orm.entity.EntityMappingHelper;import elasta.orm.entity.core.columnmapping.DirectDbColumnMapping;import elasta.orm.entity.core.columnmapping.IndirectDbColumnMapping;import elasta.orm.entity.core.columnmapping.SimpleDbColumnMapping;import elasta.orm.entity.core.columnmapping.VirtualDbColumnMapping;
import elasta.orm.entity.core.ForeignColumnMapping;
import elasta.orm.upsert.ColumnToColumnMapping;
import elasta.orm.upsert.FieldToColumnMapping;
import elasta.orm.upsert.TableDataPopulator;
import elasta.orm.upsert.impl.TableDataPopulatorImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/12/2017.
 */
final public class ListTablesToDeleteFunctionBuilderImpl implements ListTablesToDeleteFunctionBuilder {
    final EntityMappingHelper helper;

    public ListTablesToDeleteFunctionBuilderImpl(EntityMappingHelper helper) {
        Objects.requireNonNull(helper);
        this.helper = helper;
    }

    @Override
    public ListTablesToDeleteFunction build(String entity, ListTablesToDeleteFunctionBuilderContext context) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(context);

        if (context.contains(entity)) {
            return context.get(entity);
        }

        context.putEmpty(entity);

        final ListTablesToDeleteFunction listTablesToDeleteFunction = createListTablesToDeleteFunction(entity, context);

        context.put(entity, listTablesToDeleteFunction);

        return listTablesToDeleteFunction;
    }

    private ListTablesToDeleteFunction createListTablesToDeleteFunction(String entity, ListTablesToDeleteFunctionBuilderContext context) {
        ImmutableList.Builder<DirectChildHandler> directListBuilder = ImmutableList.builder();
        ImmutableList.Builder<IndirectChildHandler> indirectListBuilder = ImmutableList.builder();
        ImmutableList.Builder<VirtualChildHandler> virtualListBuilder = ImmutableList.builder();

        DbMapping dbMapping = helper.getDbMapping(entity);

        Arrays.stream(dbMapping.getDbColumnMappings())
            .filter(dbColumnMapping -> dbColumnMapping.getColumnType() != ColumnType.SIMPLE)
            .forEach(dbColumnMapping -> {
                switch (dbColumnMapping.getColumnType()) {
                    case DIRECT: {
                        DirectDbColumnMapping mapping = (DirectDbColumnMapping) dbColumnMapping;
                        directListBuilder.add(
                            new DirectChildHandlerImpl(
                                mapping.getField(),
                                listTablesToDeleteFunction(mapping.getReferencingEntity(), context)
                            )
                        );
                    }
                    break;
                    case INDIRECT: {
                        IndirectDbColumnMapping mapping = (IndirectDbColumnMapping) dbColumnMapping;
                        indirectListBuilder.add(
                            new IndirectChildHandlerImpl(
                                mapping.getField(),
                                listTablesToDeleteFunction(mapping.getReferencingEntity(), context)
                            )
                        );
                    }
                    break;
                    case VIRTUAL: {
                        VirtualDbColumnMapping mapping = (VirtualDbColumnMapping) dbColumnMapping;
                        virtualListBuilder.add(
                            new VirtualChildHandlerImpl(
                                mapping.getField(),
                                virtualColumnMappings(mapping.getForeignColumnMappingList()),
                                listTablesToDeleteFunction(mapping.getReferencingEntity(), context)
                            )
                        );
                    }
                    break;
                }
            });

        ImmutableList<DirectChildHandler> directList = directListBuilder.build();
        ImmutableList<IndirectChildHandler> indirectList = indirectListBuilder.build();
        ImmutableList<VirtualChildHandler> virtualList = virtualListBuilder.build();

        return new ListTablesToDeleteFunctionImpl(
            tableDataPopulator(dbMapping),
            directList.toArray(new DirectChildHandler[directList.size()]),
            indirectList.toArray(new IndirectChildHandler[indirectList.size()]),
            virtualList.toArray(new VirtualChildHandler[virtualList.size()])
        );
    }

    private ListTablesToDeleteFunction listTablesToDeleteFunction(String referencingEntity, ListTablesToDeleteFunctionBuilderContext context) {
        if (context.contains(referencingEntity)) {
            return context.get(referencingEntity);
        }
        if (context.isEmpty(referencingEntity)) {
            return new ProxyListTablesToDeleteFunctionImpl(referencingEntity, context);
        }
        return new ListTablesToDeleteFunctionBuilderImpl(helper).build(referencingEntity, context);
    }

    private ColumnToColumnMapping[] virtualColumnMappings(List<ForeignColumnMapping> foreignColumnMappingList) {
        ColumnToColumnMapping[] mappings = new ColumnToColumnMapping[foreignColumnMappingList.size()];
        for (int i = 0; i < foreignColumnMappingList.size(); i++) {
            mappings[i] = new ColumnToColumnMapping(
                foreignColumnMappingList.get(i).getSrcColumn(),
                foreignColumnMappingList.get(i).getDstColumn()
            );
        }
        return mappings;
    }

    private TableDataPopulator tableDataPopulator(DbMapping dbMapping) {

        List<FieldToColumnMapping> columnMappings = Arrays.stream(dbMapping.getDbColumnMappings())
            .filter(dbColumnMapping -> dbColumnMapping.getColumnType() == ColumnType.SIMPLE)
            .map(dbColumnMapping -> new FieldToColumnMapping(
                dbColumnMapping.getField(),
                ((SimpleDbColumnMapping) dbColumnMapping).getColumn()
            ))
            .collect(Collectors.toList());

        return new TableDataPopulatorImpl(
            dbMapping.getTable(),
            dbMapping.getPrimaryColumn(),
            columnMappings.toArray(new FieldToColumnMapping[columnMappings.size()])
        );
    }
}
