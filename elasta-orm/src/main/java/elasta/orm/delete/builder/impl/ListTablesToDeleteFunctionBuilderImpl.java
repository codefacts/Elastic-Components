package elasta.orm.delete.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.delete.*;
import elasta.orm.delete.builder.ListTablesToDeleteFunctionBuilder;
import elasta.orm.delete.builder.ex.ListTablesToDeleteFunctionBuilderException;
import elasta.orm.delete.impl.DirectChildHandlerImpl;
import elasta.orm.delete.impl.IndirectChildHandlerImpl;
import elasta.orm.delete.impl.ListTablesToDeleteFunctionImpl;
import elasta.orm.delete.impl.VirtualChildHandlerImpl;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.entity.core.ForeignColumnMapping;
import elasta.orm.event.builder.BuilderContext;
import elasta.sql.core.ColumnToColumnMapping;
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
    public ListTablesToDeleteFunction build(String entity, BuilderContext<ListTablesToDeleteFunction> context) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(context);

        if (context.contains(entity)) {
            return context.get(entity);
        }
        if (context.isEmpty(entity)) {
            return new ProxyListTablesToDeleteFunctionImpl(entity, context);
        }

        context.putEmpty(entity);

        final ListTablesToDeleteFunction listTablesToDeleteFunction = createListTablesToDeleteFunction(entity, context);

        context.put(entity, listTablesToDeleteFunction);

        return listTablesToDeleteFunction;
    }

    private ListTablesToDeleteFunction createListTablesToDeleteFunction(String entity, BuilderContext<ListTablesToDeleteFunction> context) {
        ImmutableList.Builder<DirectChildHandler> directListBuilder = ImmutableList.builder();
        ImmutableList.Builder<IndirectChildHandler> indirectListBuilder = ImmutableList.builder();
        ImmutableList.Builder<VirtualChildHandler> virtualListBuilder = ImmutableList.builder();

        DbMapping dbMapping = helper.getDbMapping(entity);

        DeleteUtils.getRelationMappingsForDelete(dbMapping)
            .forEach(dbColumnMapping -> {
                switch (dbColumnMapping.getColumnType()) {
                    case DIRECT: {
                        directListBuilder.add(
                            directChildHandler((DirectRelationMapping) dbColumnMapping, context)
                        );
                    }
                    break;
                    case INDIRECT: {
                        indirectListBuilder.add(
                            indirectChildHandler((IndirectRelationMapping) dbColumnMapping, context)
                        );
                    }
                    break;
                    case VIRTUAL: {
                        virtualListBuilder.add(
                            virtualChildHandler((VirtualRelationMapping) dbColumnMapping, context)
                        );
                    }
                    break;
                    default: {
                        throw new ListTablesToDeleteFunctionBuilderException("Invalid columnType '" + dbColumnMapping + "' in '" + entity + "." + dbColumnMapping.getField() + "'");
                    }
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

    private DirectChildHandler directChildHandler(DirectRelationMapping mapping, BuilderContext<ListTablesToDeleteFunction> context) {
        return new DirectChildHandlerImpl(
            mapping.getField(),
            listTablesToDeleteFunction(mapping.getReferencingEntity(), context)
        );
    }

    private IndirectChildHandler indirectChildHandler(IndirectRelationMapping mapping, BuilderContext<ListTablesToDeleteFunction> context) {
        return new IndirectChildHandlerImpl(
            mapping.getField(),
            listTablesToDeleteFunction(mapping.getReferencingEntity(), context)
        );
    }

    private VirtualChildHandler virtualChildHandler(VirtualRelationMapping mapping, BuilderContext<ListTablesToDeleteFunction> context) {
        return new VirtualChildHandlerImpl(
            mapping.getField(),
            virtualColumnMappings(mapping.getForeignColumnMappingList()),
            listTablesToDeleteFunction(mapping.getReferencingEntity(), context)
        );
    }

    private ListTablesToDeleteFunction listTablesToDeleteFunction(String referencingEntity, BuilderContext<ListTablesToDeleteFunction> context) {
        return build(referencingEntity, context);
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

        List<FieldToColumnMapping> columnMappings = Arrays.stream(dbMapping.getColumnMappings())
            .map(dbColumnMapping -> new FieldToColumnMapping(
                dbColumnMapping.getField(),
                dbColumnMapping.getColumn()
            ))
            .collect(Collectors.toList());

        return new TableDataPopulatorImpl(
            dbMapping.getTable(),
            dbMapping.getPrimaryColumn(),
            columnMappings.toArray(new FieldToColumnMapping[columnMappings.size()])
        );
    }
}
