package elasta.orm.upsert.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.core.promise.intfs.Promise;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.event.builder.BuilderContext;
import elasta.orm.upsert.ForeignColumnMapping;
import elasta.orm.upsert.builder.UpsertFunctionBuilder;
import elasta.orm.upsert.*;
import elasta.orm.upsert.builder.ex.UpserFunctionBuilderException;
import elasta.orm.upsert.impl.*;
import elasta.sql.core.ColumnToColumnMapping;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-21.
 */
public class UpsertFunctionBuilderImpl implements UpsertFunctionBuilder {
    private final EntityMappingHelper helper;
    private final IdGenerator idGenerator;

    public UpsertFunctionBuilderImpl(EntityMappingHelper entityMappingHelper, IdGenerator idGenerator) {
        Objects.requireNonNull(entityMappingHelper);
        Objects.requireNonNull(idGenerator);
        this.helper = entityMappingHelper;
        this.idGenerator = idGenerator;
    }

    @Override
    public UpsertFunction build(String entity, BuilderContext<UpsertFunction> context) {
        return new QQ(context).build(entity);
    }

    private final class QQ {
        final BuilderContext<UpsertFunction> context;

        QQ(BuilderContext<UpsertFunction> context) {
            Objects.requireNonNull(context);
            this.context = context;
        }

        public UpsertFunction build(String entity) {
            Objects.requireNonNull(entity);

            if (context.contains(entity)) {
                return context.get(entity);
            }

            if (context.isEmpty(entity)) {
                return new ProxyUpsertFunctionImpl(entity, context);
            }

            context.putEmpty(entity);

            UpsertFunction upsertFunction = buildUpsertFunction(entity);

            context.put(entity, upsertFunction);

            return upsertFunction;
        }

        private UpsertFunction buildUpsertFunction(String entity) {
            DbMapping dbMapping = helper.getDbMapping(entity);

            List<FieldToColumnMapping> list = Arrays.stream(dbMapping.getColumnMappings())
                .map(dbColumnMapping -> new FieldToColumnMapping(dbColumnMapping.getField(), Utils.<ColumnMapping>cast(dbColumnMapping).getColumn()))
                .collect(Collectors.toList());

            TableDataPopulatorImpl tableDataPopulator = new TableDataPopulatorImpl(
                dbMapping.getTable(),
                dbMapping.getPrimaryColumn(),
                list.toArray(new FieldToColumnMapping[list.size()])
            );

            ImmutableList.Builder<DirectDependency> directDependencyBuilder = ImmutableList.builder();
            ImmutableList.Builder<IndirectDependency> indirectDependencyBuilder = ImmutableList.builder();
            ImmutableList.Builder<BelongsTo> belongsToBuilder = ImmutableList.builder();

            UpsertUtils.getRelationMappingsForUpsert(dbMapping).forEach(dbColumnMapping -> {

                switch (dbColumnMapping.getColumnType()) {
                    case DIRECT: {
                        directDependencyBuilder.add(
                            makeDirectMapping(
                                (DirectRelationMapping) dbColumnMapping
                            )
                        );
                        return;
                    }
                    case INDIRECT: {
                        indirectDependencyBuilder.add(
                            makeIndirectDepedency(
                                (IndirectRelationMapping) dbColumnMapping
                            )
                        );
                        return;
                    }
                    case VIRTUAL: {
                        belongsToBuilder.add(
                            makeBelongTo(
                                (VirtualRelationMapping) dbColumnMapping
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
            final ImmutableList<IndirectDependency> indirectDependencies = indirectDependencyBuilder.build();
            final ImmutableList<BelongsTo> belongsTos = belongsToBuilder.build();

            return new UpsertFunctionImpl(
                entity,
                helper.getPrimaryKey(entity),
                tableDataPopulator,
                directDependencies.toArray(new DirectDependency[directDependencies.size()]),
                indirectDependencies.toArray(new IndirectDependency[indirectDependencies.size()]),
                belongsTos.toArray(new BelongsTo[belongsTos.size()]),
                idGenerator
            );
        }

        private DirectDependency makeDirectMapping(DirectRelationMapping mapping) {

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

        private IndirectDependency makeIndirectDepedency(IndirectRelationMapping indirectRelationMapping) {

            List<ColumnToColumnMapping> srcMappings = indirectRelationMapping.getSrcForeignColumnMappingList().stream()
                .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn(), foreignColumnMapping.getDstColumn()))
                .collect(Collectors.toList());

            List<ColumnToColumnMapping> dstMappings = indirectRelationMapping.getDstForeignColumnMappingList().stream()
                .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn(), foreignColumnMapping.getDstColumn()))
                .collect(Collectors.toList());

            return new IndirectDependency(
                indirectRelationMapping.getField(),
                new IndirectDependencyHandlerImpl(
                    new RelationTableUpserFunctionImpl(
                        new RelationTableDataPopulatorImpl(
                            indirectRelationMapping.getRelationTable(),
                            srcMappings.toArray(new ColumnToColumnMapping[srcMappings.size()]),
                            dstMappings.toArray(new ColumnToColumnMapping[dstMappings.size()])
                        )
                    ),
                    createUpsertFunction(indirectRelationMapping.getReferencingEntity())
                )
            );
        }

        private UpsertFunction createUpsertFunction(final String referencingEntity) {
            return build(referencingEntity);
        }

        private BelongsTo makeBelongTo(VirtualRelationMapping mapping) {

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
        final BuilderContext<UpsertFunction> functionMap;

        ProxyUpsertFunctionImpl(String referencingEntity, BuilderContext<UpsertFunction> context) {
            this.referencingEntity = referencingEntity;
            this.functionMap = context;
        }

        @Override
        public Promise<TableData> upsert(JsonObject jsonObject, UpsertContext upsertContext) {
            return functionMap.get(referencingEntity).upsert(jsonObject, upsertContext);
        }
    }
}
