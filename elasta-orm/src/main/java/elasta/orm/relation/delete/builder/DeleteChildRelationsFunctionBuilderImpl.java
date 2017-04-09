package elasta.orm.relation.delete.builder;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.event.builder.BuilderContext;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;
import elasta.orm.relation.delete.RelationFieldHandler;
import elasta.orm.relation.delete.builder.ex.DeleteChildRelationsFunctionBuilderException;
import elasta.orm.relation.delete.impl.*;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/8/2017.
 */
final public class DeleteChildRelationsFunctionBuilderImpl implements DeleteChildRelationsFunctionBuilder {
    final EntityMappingHelper helper;

    public DeleteChildRelationsFunctionBuilderImpl(EntityMappingHelper helper) {
        Objects.requireNonNull(helper);
        this.helper = helper;
    }

    @Override
    public DeleteChildRelationsFunction build(String entity, BuilderContext<DeleteChildRelationsFunction> context) {
        if (context.contains(entity)) {
            return context.get(entity);
        }
        if (context.isEmpty(entity)) {
            return new ProxyDeleteChildRelationsFunctionImpl(entity, context);
        }

        context.putEmpty(entity);

        DeleteChildRelationsFunction function = createFunction(entity, context);

        context.put(entity, function);

        return function;
    }

    private DeleteChildRelationsFunction createFunction(String entity, BuilderContext<DeleteChildRelationsFunction> context) {

        ImmutableList.Builder<RelationFieldHandler> listBuilder = ImmutableList.builder();

        DbMapping dbMapping = helper.getDbMapping(entity);

        Arrays.stream(dbMapping.getDbColumnMappings())
            .filter(dbColumnMapping -> dbColumnMapping.getColumnType() != ColumnType.SIMPLE)
            .map(dbColumnMapping -> relationFieldHandler(entity, dbColumnMapping, context))
            .forEach(listBuilder::add);

        return new DeleteChildRelationsFunctionImpl(
            listBuilder.build()
        );
    }

    private RelationFieldHandler relationFieldHandler(String entity, DbColumnMapping dbColumnMapping, BuilderContext<DeleteChildRelationsFunction> context) {
        switch (dbColumnMapping.getColumnType()) {
            case INDIRECT: {
                return indirectRelation(entity, (IndirectDbColumnMapping) dbColumnMapping, context);
            }
            case DIRECT: {
                return directRelation(entity, (DirectDbColumnMapping) dbColumnMapping, context);
            }
            case VIRTUAL: {
                return virtualRelation(entity, (VirtualDbColumnMapping) dbColumnMapping, context);
            }
        }
        throw new DeleteChildRelationsFunctionBuilderException("Invalid dbColumnMapping '" + dbColumnMapping + "'");
    }

    private RelationFieldHandler virtualRelation(String entity, VirtualDbColumnMapping dbColumnMapping, BuilderContext<DeleteChildRelationsFunction> context) {
        return new VirtualChildRelationFieldHandlerImpl(
            dbColumnMapping.getField(),
            helper.getField(entity, dbColumnMapping.getField()).getJavaType(),
            dbColumnMapping.getReferencingTable(),
            referencingTablePrimaryColumnToFieldMappings(dbColumnMapping),
            relationTableRelationColumns(dbColumnMapping),
            build(dbColumnMapping.getReferencingEntity(), context)
        );
    }

    private List<String> relationTableRelationColumns(VirtualDbColumnMapping dbColumnMapping) {
        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();

        dbColumnMapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
            listBuilder.add(foreignColumnMapping.getSrcColumn());
        });

        return listBuilder.build();
    }

    private List<ColumnToFieldMapping> referencingTablePrimaryColumnToFieldMappings(VirtualDbColumnMapping dbColumnMapping) {
        SimpleDbColumnMapping primaryKeyColumnMapping = helper.getPrimaryKeyColumnMapping(dbColumnMapping.getReferencingEntity());
        return ImmutableList.of(
            new ColumnToFieldMapping(
                primaryKeyColumnMapping.getField(),
                primaryKeyColumnMapping.getColumn()
            )
        );
    }

    private RelationFieldHandler directRelation(String entity, DirectDbColumnMapping dbColumnMapping, BuilderContext<DeleteChildRelationsFunction> context) {
        return new DirectChildRelationFieldHandlerImpl(
            dbColumnMapping.getField(),
            helper.getTable(entity),
            primaryColumnToFieldMappings(entity),
            referencingColumns(dbColumnMapping),
            this.build(dbColumnMapping.getReferencingEntity(), context)
        );
    }

    private List<String> referencingColumns(DirectDbColumnMapping dbColumnMapping) {
        ImmutableList.Builder<String> listBuilder = ImmutableList.builder();

        dbColumnMapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> listBuilder.add(foreignColumnMapping.getSrcColumn()));

        return listBuilder.build();
    }

    private List<ColumnToFieldMapping> primaryColumnToFieldMappings(String entity) {
        SimpleDbColumnMapping primaryKeyColumnMapping = helper.getPrimaryKeyColumnMapping(entity);
        return ImmutableList.of(
            new ColumnToFieldMapping(
                primaryKeyColumnMapping.getField(),
                primaryKeyColumnMapping.getColumn()
            )
        );
    }

    private RelationFieldHandler indirectRelation(String entity, IndirectDbColumnMapping dbColumnMapping, BuilderContext<DeleteChildRelationsFunction> context) {
        return new IndirectChildRelationFieldHandlerImpl(
            dbColumnMapping.getField(),
            helper.getField(entity, dbColumnMapping.getField()).getJavaType(),
            dbColumnMapping.getRelationTable(),
            srcSrcFieldToRelationTableColumnMappings(entity, dbColumnMapping),
            dstSrcFieldToRelationTableColumnMappings(entity, dbColumnMapping),
            build(dbColumnMapping.getReferencingEntity(), context)
        );
    }

    private List<SrcFieldToRelationTableColumnMapping> dstSrcFieldToRelationTableColumnMappings(String entity, IndirectDbColumnMapping dbColumnMapping) {
        ImmutableList.Builder<SrcFieldToRelationTableColumnMapping> listBuilder = ImmutableList.builder();

        dbColumnMapping.getDstForeignColumnMappingList().forEach(foreignColumnMapping -> {
            listBuilder.add(
                new SrcFieldToRelationTableColumnMapping(
                    helper.getFieldByColumn(dbColumnMapping.getReferencingEntity(), foreignColumnMapping.getSrcColumn()).getName(),
                    foreignColumnMapping.getDstColumn()
                )
            );
        });

        return listBuilder.build();
    }

    private List<SrcFieldToRelationTableColumnMapping> srcSrcFieldToRelationTableColumnMappings(String entity, IndirectDbColumnMapping dbColumnMapping) {
        ImmutableList.Builder<SrcFieldToRelationTableColumnMapping> listBuilder = ImmutableList.builder();

        dbColumnMapping.getSrcForeignColumnMappingList().forEach(foreignColumnMapping -> {
            listBuilder.add(
                new SrcFieldToRelationTableColumnMapping(
                    helper.getFieldByColumn(entity, foreignColumnMapping.getSrcColumn()).getName(),
                    foreignColumnMapping.getDstColumn()
                )
            );
        });

        return listBuilder.build();
    }
}
