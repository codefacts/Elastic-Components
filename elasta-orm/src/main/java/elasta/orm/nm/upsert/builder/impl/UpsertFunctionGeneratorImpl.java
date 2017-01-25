package elasta.orm.nm.upsert.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.orm.nm.entitymodel.*;
import elasta.orm.nm.entitymodel.columnmapping.*;
import elasta.orm.nm.upsert.*;
import elasta.orm.nm.upsert.ForeignColumnMapping;
import elasta.orm.nm.upsert.builder.FunctionMap;
import elasta.orm.nm.upsert.builder.UpsertFunctionGenerator;
import elasta.orm.nm.upsert.impl.*;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 2017-01-21.
 */
public class UpsertFunctionGeneratorImpl implements UpsertFunctionGenerator {
    final EntityMappingHelper helper;
    final FunctionMap functionMap;

    public UpsertFunctionGeneratorImpl(EntityMappingHelper entityMappingHelper, FunctionMap functionMap) {
        Objects.requireNonNull(entityMappingHelper);
        Objects.requireNonNull(functionMap);
        this.helper = entityMappingHelper;
        this.functionMap = functionMap;
    }

    @Override
    public UpsertFunction create(String entity) {

        functionMap.putEmpty(entity);

        DbMapping dbMapping = helper.getDbMapping(entity);

        List<FieldToColumnMapping> list = Arrays.asList(dbMapping.getDbColumnMappings()).stream()
            .filter(dbColumnMapping -> dbColumnMapping.getColumnType() == ColumnType.SIMPLE)
            .map(dbColumnMapping -> new FieldToColumnMapping(dbColumnMapping.getField(), Utils.<SimpleColumnMapping>cast(dbColumnMapping).getColumn()))
            .collect(Collectors.toList());

        TableDataPopulatorImpl tableDataPopulator = new TableDataPopulatorImpl(
            dbMapping.getTable(),
            dbMapping.getPrimaryColumn(),
            list.toArray(new FieldToColumnMapping[list.size()])
        );

        ImmutableList.Builder<DirectDependency> directDependencyBuilder = ImmutableList.builder();
        ImmutableList.Builder<IndirectDependency> inDirectDependencyBuilder = ImmutableList.builder();
        ImmutableList.Builder<BelongsTo> belongsToBuilder = ImmutableList.builder();

        Arrays.asList(dbMapping.getDbColumnMappings()).stream().filter(dbColumnMapping -> dbColumnMapping.getColumnType() != ColumnType.SIMPLE)
            .forEach(dbColumnMapping -> {

                if (dbColumnMapping.getColumnType() == ColumnType.DIRECT) {

                    directDependencyBuilder.add(
                        makeDirectMapping(
                            (DirectColumnMapping) dbColumnMapping
                        )
                    );

                } else if (dbColumnMapping.getColumnType() == ColumnType.INDIRECT) {

                    inDirectDependencyBuilder.add(
                        makeIndirectDepedency(
                            (IndirectColumnMapping) dbColumnMapping
                        )
                    );

                } else if (dbColumnMapping.getColumnType() == ColumnType.VIRTUAL) {

                    belongsToBuilder.add(
                        makeBelongTo(
                            (VirtualColumnMapping) dbColumnMapping
                        )
                    );
                }

            });

        final ImmutableList<DirectDependency> directDependencies = directDependencyBuilder.build();
        final ImmutableList<IndirectDependency> indirectDependencies = inDirectDependencyBuilder.build();
        final ImmutableList<BelongsTo> belongsTos = belongsToBuilder.build();

        UpsertFunctionImpl upsertFunction = new UpsertFunctionImpl(
            entity,
            tableDataPopulator,
            directDependencies.toArray(new DirectDependency[directDependencies.size()]),
            indirectDependencies.toArray(new IndirectDependency[indirectDependencies.size()]),
            belongsTos.toArray(new BelongsTo[belongsTos.size()])
        );

        functionMap.put(entity, upsertFunction);

        return upsertFunction;
    }

    private DirectDependency makeDirectMapping(DirectColumnMapping mapping) {

        List<ForeignColumnMapping> foreignColumnMappings = mapping.getForeignColumnMappingList().stream()
            .map(foreignColumnMapping -> new ForeignColumnMapping(
                foreignColumnMapping.getSrcColumn().getName(),
                foreignColumnMapping.getDstColumn().getName()
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

    private IndirectDependency makeIndirectDepedency(IndirectColumnMapping indirectColumnMapping) {

        ImmutableList.Builder<ForeignColumnMapping> foreignColumnMappingBuilder = ImmutableList.builder();

        List<ColumnToColumnMapping> srcMappings = indirectColumnMapping.getSrcForeignColumnMappingList().stream()
            .peek(foreignColumnMapping -> foreignColumnMappingBuilder.add(
                new ForeignColumnMapping(foreignColumnMapping.getSrcColumn().getName(), foreignColumnMapping.getDstColumn().getName())
            ))
            .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn().getName(), foreignColumnMapping.getDstColumn().getName()))
            .collect(Collectors.toList());

        List<ColumnToColumnMapping> dstMappings = indirectColumnMapping.getDstForeignColumnMappingList().stream()
            .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn().getName(), foreignColumnMapping.getDstColumn().getName()))
            .collect(Collectors.toList());

        ImmutableList<ForeignColumnMapping> foreignColumnMappings = foreignColumnMappingBuilder.build();

        return new IndirectDependency(
            indirectColumnMapping.getField(),
            new IndirectDependencyHandlerImpl(
                new RelationTableUpserFunctionImpl(
                    new RelationTableDataPopulatorImpl(
                        indirectColumnMapping.getRelationTable(),
                        srcMappings.toArray(new ColumnToColumnMapping[srcMappings.size()]),
                        dstMappings.toArray(new ColumnToColumnMapping[dstMappings.size()])
                    )
                ),
                createUpsertFunction(indirectColumnMapping.getReferencingEntity())
            )
        );
    }

    private UpsertFunction createUpsertFunction(final String referencingEntity) {

        if (not(functionMap.exists(referencingEntity))) {

            functionMap.put(referencingEntity, new UpsertFunctionGeneratorImpl(helper, functionMap).create(referencingEntity));
        }

        return new UpsertFunctionImpl2(referencingEntity, functionMap);
    }

    private BelongsTo makeBelongTo(VirtualColumnMapping mapping) {

        List<ForeignColumnMapping> foreignColumnMappings = mapping.getForeignColumnMappingList().stream()
            .map(foreignColumnMapping -> new ForeignColumnMapping(foreignColumnMapping.getSrcColumn().getName(), foreignColumnMapping.getDstColumn().getName()))
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

    private static class UpsertFunctionImpl2 implements UpsertFunction {
        final String referencingEntity;
        final FunctionMap functionMap;

        public UpsertFunctionImpl2(String referencingEntity, FunctionMap functionMap) {
            this.referencingEntity = referencingEntity;
            this.functionMap = functionMap;
        }

        @Override
        public TableData upsert(JsonObject jsonObject, UpsertContext upsertContext) {
            return functionMap.get(referencingEntity).upsert(jsonObject, upsertContext);
        }
    }
}
