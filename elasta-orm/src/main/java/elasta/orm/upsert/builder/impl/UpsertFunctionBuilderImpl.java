package elasta.orm.upsert.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.upsert.ForeignColumnMapping;
import elasta.orm.upsert.builder.FunctionMap;
import elasta.orm.upsert.builder.UpsertFunctionBuilder;
import elasta.orm.upsert.*;
import elasta.orm.upsert.builder.ex.UpserFunctionBuilderException;
import elasta.orm.upsert.impl.*;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 2017-01-21.
 */
public class UpsertFunctionBuilderImpl implements UpsertFunctionBuilder {
    final EntityMappingHelper helper;

    public UpsertFunctionBuilderImpl(EntityMappingHelper entityMappingHelper) {
        Objects.requireNonNull(entityMappingHelper);
        this.helper = entityMappingHelper;
    }

    @Override
    public UpsertFunction build(String entity, FunctionMap<UpsertFunction> functionMap) {
        return new QQ(functionMap).build(entity);
    }

    private final class QQ {
        final FunctionMap<UpsertFunction> functionMap;

        public QQ(FunctionMap<UpsertFunction> functionMap) {
            Objects.requireNonNull(functionMap);
            this.functionMap = functionMap;
        }

        public UpsertFunction build(String entity) {
            functionMap.putEmpty(entity);

            UpsertFunction upsertFunction = buildUpsertFunction(entity);

            functionMap.put(entity, upsertFunction);

            return upsertFunction;
        }

        private UpsertFunction buildUpsertFunction(String entity) {
            DbMapping dbMapping = helper.getDbMapping(entity);

            List<FieldToColumnMapping> list = Arrays.asList(dbMapping.getDbColumnMappings()).stream()
                .filter(dbColumnMapping -> dbColumnMapping.getColumnType() == ColumnType.SIMPLE)
                .map(dbColumnMapping -> new FieldToColumnMapping(dbColumnMapping.getField(), Utils.<SimpleDbColumnMapping>cast(dbColumnMapping).getColumn()))
                .collect(Collectors.toList());

            TableDataPopulatorImpl tableDataPopulator = new TableDataPopulatorImpl(
                dbMapping.getTable(),
                dbMapping.getPrimaryColumn(),
                list.toArray(new FieldToColumnMapping[list.size()])
            );

            ImmutableList.Builder<DirectDependency> directDependencyBuilder = ImmutableList.builder();
            ImmutableList.Builder<IndirectDependency> inDirectDependencyBuilder = ImmutableList.builder();
            ImmutableList.Builder<BelongsTo> belongsToBuilder = ImmutableList.builder();

            UpsertUtils.getRelationMappingsForUpsert(dbMapping).forEach(dbColumnMapping -> {

                switch (dbColumnMapping.getColumnType()) {
                    case DIRECT: {
                        directDependencyBuilder.add(
                            makeDirectMapping(
                                (DirectDbColumnMapping) dbColumnMapping
                            )
                        );
                        return;
                    }
                    case INDIRECT: {
                        inDirectDependencyBuilder.add(
                            makeIndirectDepedency(
                                (IndirectDbColumnMapping) dbColumnMapping
                            )
                        );
                        return;
                    }
                    case VIRTUAL: {
                        belongsToBuilder.add(
                            makeBelongTo(
                                (VirtualDbColumnMapping) dbColumnMapping
                            )
                        );
                        return;
                    }
                    default: {
                        throw new UpserFunctionBuilderException("Invalid columnType '" + dbColumnMapping.getColumnType() + "' in '" + entity + "." + dbColumnMapping.getField() + "'");
                    }
                }
            });

            final ImmutableList<DirectDependency> directDependencies = directDependencyBuilder.build();
            final ImmutableList<IndirectDependency> indirectDependencies = inDirectDependencyBuilder.build();
            final ImmutableList<BelongsTo> belongsTos = belongsToBuilder.build();

            return new UpsertFunctionImpl(
                entity,
                tableDataPopulator,
                directDependencies.toArray(new DirectDependency[directDependencies.size()]),
                indirectDependencies.toArray(new IndirectDependency[indirectDependencies.size()]),
                belongsTos.toArray(new BelongsTo[belongsTos.size()])
            );
        }

        private DirectDependency makeDirectMapping(DirectDbColumnMapping mapping) {

            List<ForeignColumnMapping> foreignColumnMappings = mapping.getForeignColumnMappingList().stream()
                .map(foreignColumnMapping -> new ForeignColumnMapping(
                    foreignColumnMapping.getSrcColumn(),
                    foreignColumnMapping.getDstColumn()
                )).collect(Collectors.toList());

            return new DirectDependency(
                mapping.getField(),
                new DirectDependencyHandlerImpl(
                    createUpsertFunction(mapping.getReferencingEntity())
                ),
                new DependencyColumnValuePopulatorImpl(
                    foreignColumnMappings.toArray(new ForeignColumnMapping[foreignColumnMappings.size()])
                )
            );
        }

        private IndirectDependency makeIndirectDepedency(IndirectDbColumnMapping indirectDbColumnMapping) {

            ImmutableList.Builder<ForeignColumnMapping> foreignColumnMappingBuilder = ImmutableList.builder();

            List<ColumnToColumnMapping> srcMappings = indirectDbColumnMapping.getSrcForeignColumnMappingList().stream()
                .peek(foreignColumnMapping -> foreignColumnMappingBuilder.add(
                    new ForeignColumnMapping(foreignColumnMapping.getSrcColumn(), foreignColumnMapping.getDstColumn())
                ))
                .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn(), foreignColumnMapping.getDstColumn()))
                .collect(Collectors.toList());

            List<ColumnToColumnMapping> dstMappings = indirectDbColumnMapping.getDstForeignColumnMappingList().stream()
                .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn(), foreignColumnMapping.getDstColumn()))
                .collect(Collectors.toList());

            ImmutableList<ForeignColumnMapping> foreignColumnMappings = foreignColumnMappingBuilder.build();

            return new IndirectDependency(
                indirectDbColumnMapping.getField(),
                new IndirectDependencyHandlerImpl(
                    new RelationTableUpserFunctionImpl(
                        new RelationTableDataPopulatorImpl(
                            indirectDbColumnMapping.getRelationTable(),
                            srcMappings.toArray(new ColumnToColumnMapping[srcMappings.size()]),
                            dstMappings.toArray(new ColumnToColumnMapping[dstMappings.size()])
                        )
                    ),
                    createUpsertFunction(indirectDbColumnMapping.getReferencingEntity())
                )
            );
        }

        private UpsertFunction createUpsertFunction(final String referencingEntity) {

            if (not(functionMap.exists(referencingEntity))) {

                functionMap.put(referencingEntity, new UpsertFunctionBuilderImpl(helper).build(referencingEntity, functionMap));
            }

            return new ProxyUpsertFunctionImpl(referencingEntity, functionMap);
        }

        private BelongsTo makeBelongTo(VirtualDbColumnMapping mapping) {

            List<ForeignColumnMapping> foreignColumnMappings = mapping.getForeignColumnMappingList().stream()
                .map(foreignColumnMapping -> new ForeignColumnMapping(foreignColumnMapping.getSrcColumn(), foreignColumnMapping.getDstColumn()))
                .collect(Collectors.toList());

            return new BelongsTo(
                mapping.getField(),
                new BelongToHandlerImpl(
                    createUpsertFunction(mapping.getReferencingEntity())
                ),
                new DependencyColumnValuePopulatorImpl(
                    foreignColumnMappings.toArray(new ForeignColumnMapping[foreignColumnMappings.size()])
                )
            );
        }
    }

    private static class ProxyUpsertFunctionImpl implements UpsertFunction {
        final String referencingEntity;
        final FunctionMap<UpsertFunction> functionMap;

        public ProxyUpsertFunctionImpl(String referencingEntity, FunctionMap functionMap) {
            this.referencingEntity = referencingEntity;
            this.functionMap = functionMap;
        }

        @Override
        public TableData upsert(JsonObject jsonObject, UpsertContext upsertContext) {
            return functionMap.get(referencingEntity).upsert(jsonObject, upsertContext);
        }
    }
}
