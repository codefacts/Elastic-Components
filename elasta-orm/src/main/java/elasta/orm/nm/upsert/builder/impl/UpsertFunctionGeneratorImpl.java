package elasta.orm.nm.upsert.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.orm.nm.entitymodel.*;
import elasta.orm.nm.entitymodel.columnmapping.*;
import elasta.orm.nm.upsert.*;
import elasta.orm.nm.upsert.ForeignColumnMapping;
import elasta.orm.nm.upsert.builder.UpsertFunctionGenerator;
import elasta.orm.nm.upsert.impl.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-21.
 */
public class UpsertFunctionGeneratorImpl implements UpsertFunctionGenerator {
    final EntityMappingHelper helper;

    public UpsertFunctionGeneratorImpl(EntityMappingHelper entityMappingHelper) {
        this.helper = entityMappingHelper;
    }

    @Override
    public UpsertFunction create(String entity) {

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

        return new UpsertFunctionImpl(
            entity,
            tableDataPopulator,
            directDependencies.toArray(new DirectDependency[directDependencies.size()]),
            indirectDependencies.toArray(new IndirectDependency[indirectDependencies.size()]),
            belongsTos.toArray(new BelongsTo[belongsTos.size()])
        );
    }

    private DirectDependency makeDirectMapping(DirectColumnMapping mapping) {

        List<ForeignColumnMapping> foreignColumnMappings = mapping.getForeignColumnMappingList().stream().map(foreignColumnMapping -> new ForeignColumnMapping(
            foreignColumnMapping.getSrcColumn().getName(),
            foreignColumnMapping.getDstColumn().getName()
        )).collect(Collectors.toList());

        return new DirectDependency(
            mapping.getField(),
            new DirectDependencyHandlerImpl(
                new UpsertFunctionGeneratorImpl(helper).create(mapping.getReferencingEntity())
            ),
            new DependencyColumnValuePopulatorImpl(
                foreignColumnMappings.toArray(new ForeignColumnMapping[foreignColumnMappings.size()])
            )
        );
    }

    private IndirectDependency makeIndirectDepedency(IndirectColumnMapping indirectColumnMapping) {

        List<ColumnToColumnMapping> srcMappings = indirectColumnMapping.getSrcForeignColumnMappingList().stream()
            .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn().getName(), foreignColumnMapping.getDstColumn().getName()))
            .collect(Collectors.toList());

        List<ColumnToColumnMapping> dstMappings = indirectColumnMapping.getDstForeignColumnMappingList().stream()
            .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn().getName(), foreignColumnMapping.getDstColumn().getName()))
            .collect(Collectors.toList());

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
                new UpsertFunctionGeneratorImpl(helper).create(indirectColumnMapping.getReferencingEntity())
            ),
            new DependencyColumnValuePopulatorImpl(null)
        );
    }

    private BelongsTo makeBelongTo(VirtualColumnMapping mapping) {

        List<ForeignColumnMapping> foreignColumnMappings = mapping.getForeignColumnMappingList().stream()
            .map(foreignColumnMapping -> new ForeignColumnMapping(foreignColumnMapping.getSrcColumn().getName(), foreignColumnMapping.getDstColumn().getName()))
            .collect(Collectors.toList());

        return new BelongsTo(
            mapping.getField(),
            new BelongToHandlerImpl(
                new UpsertFunctionGeneratorImpl(helper).create(mapping.getReferencingEntity())
            ),
            new DependencyColumnValuePopulatorImpl(
                foreignColumnMappings.toArray(new ForeignColumnMapping[foreignColumnMappings.size()])
            )
        );
    }
}
