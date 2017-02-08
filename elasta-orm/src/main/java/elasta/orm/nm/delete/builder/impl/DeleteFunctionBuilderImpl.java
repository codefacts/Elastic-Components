package elasta.orm.nm.delete.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.nm.delete.*;
import elasta.orm.nm.delete.builder.DeleteFunctionBuilder;
import elasta.orm.nm.delete.impl.*;
import elasta.orm.nm.entitymodel.*;
import elasta.orm.nm.entitymodel.columnmapping.DbColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.IndirectColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.SimpleColumnMapping;
import elasta.orm.nm.upsert.ColumnToColumnMapping;
import elasta.orm.nm.upsert.FieldToColumnMapping;
import elasta.orm.nm.upsert.builder.FunctionMap;
import elasta.orm.nm.upsert.impl.RelationTableDataPopulatorImpl;
import elasta.orm.nm.upsert.impl.TableDataPopulatorImpl;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 17/02/07.
 */
final public class DeleteFunctionBuilderImpl implements DeleteFunctionBuilder {
    final EntityMappingHelper helper;
    final FunctionMap<DeleteFunction> functionMap;

    public DeleteFunctionBuilderImpl(EntityMappingHelper helper, FunctionMap<DeleteFunction> functionMap) {
        Objects.requireNonNull(helper);
        Objects.requireNonNull(functionMap);
        this.helper = helper;
        this.functionMap = functionMap;
    }

    @Override
    public DeleteFunction create(final String entity) {

        Entity ee = helper.getEntity(entity);

        ImmutableList.Builder<BelongsToDeleteDependency> belongsToDeleteDependencyListBuilder = ImmutableList.builder();
        ImmutableList.Builder<DirectDeleteDependency> directDeleteDependencyListBuilder = ImmutableList.builder();
        ImmutableList.Builder<IndirectDeleteDependency> indirectDeleteDependencyListBuilder = ImmutableList.builder();

        Map<String, DbColumnMapping> mappingMap = helper.getFieldToColumnMappingMap(entity);

        Arrays.asList(ee.getFields()).stream()
            .filter(field -> field.getRelationship().isPresent())
            .forEach(field -> {
                Relationship relationship = field.getRelationship().get();
                final DbColumnMapping mapping = mappingMap.get(field.getName());

                switch (relationship.getType()) {
                    case ONE_TO_ONE:
                        new OneToOneHandler(
                            ee,
                            belongsToDeleteDependencyListBuilder,
                            directDeleteDependencyListBuilder,
                            indirectDeleteDependencyListBuilder,
                            mappingMap,
                            mapping,
                            relationship
                        ).handle(field);
                        break;
                    case MANY_TO_ONE:
                        new ManyToOneHandler(
                            ee,
                            belongsToDeleteDependencyListBuilder,
                            directDeleteDependencyListBuilder,
                            indirectDeleteDependencyListBuilder,
                            mappingMap,
                            mapping,
                            relationship
                        ).handle(field);
                        break;
                }
            });

        return null;
    }

    private DeleteFunction deleteFunction(final String entity) {
        return null;
    }

    private class OneToOneHandler {
        final Entity ee;
        final ImmutableList.Builder<BelongsToDeleteDependency> belongsToDeleteDependencyListBuilder;
        final ImmutableList.Builder<DirectDeleteDependency> directDeleteDependencyListBuilder;
        final ImmutableList.Builder<IndirectDeleteDependency> indirectDeleteDependencyListBuilder;
        final Map<String, DbColumnMapping> mappingMap;
        final DbColumnMapping mapping;
        final Relationship relationship;

        private OneToOneHandler(Entity ee, ImmutableList.Builder<BelongsToDeleteDependency> belongsToDeleteDependencyListBuilder, ImmutableList.Builder<DirectDeleteDependency> directDeleteDependencyListBuilder, ImmutableList.Builder<IndirectDeleteDependency> indirectDeleteDependencyListBuilder, Map<String, DbColumnMapping> mappingMap, DbColumnMapping mapping, Relationship relationship) {
            this.ee = ee;
            this.belongsToDeleteDependencyListBuilder = belongsToDeleteDependencyListBuilder;
            this.directDeleteDependencyListBuilder = directDeleteDependencyListBuilder;
            this.indirectDeleteDependencyListBuilder = indirectDeleteDependencyListBuilder;
            this.mappingMap = mappingMap;
            this.mapping = mapping;
            this.relationship = relationship;
        }

        public void handle(Field field) {
            if (mapping.getColumnType() == ColumnType.VIRTUAL) {

                belongsToDeleteDependencyListBuilder.add(
                    new BelongsToDeleteDependency(new BelongsToDeleteHandlerImpl(deleteFunction(relationship.getEntity())), field.getName())
                );

            } else if (mapping.getColumnType() == ColumnType.DIRECT) {

                directDeleteDependencyListBuilder.add(
                    new DirectDeleteDependency(field.getName(), new DirectDeleteHandlerImpl(deleteFunction(relationship.getEntity())))
                );

            } else if (mapping.getColumnType() == ColumnType.INDIRECT) {

                final IndirectColumnMapping indirectColumnMapping = (IndirectColumnMapping) mapping;

                List<ColumnToColumnMapping> srcMappings = indirectColumnMapping.getSrcForeignColumnMappingList().stream()
                    .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn().getName(), foreignColumnMapping.getDstColumn().getName()))
                    .collect(Collectors.toList());

                List<ColumnToColumnMapping> dstMappings = indirectColumnMapping.getSrcForeignColumnMappingList().stream()
                    .map(foreignColumnMapping -> new ColumnToColumnMapping(foreignColumnMapping.getSrcColumn().getName(), foreignColumnMapping.getDstColumn().getName()))
                    .collect(Collectors.toList());

                RelationTableDataPopulatorImpl relationTableDataPopulator = new RelationTableDataPopulatorImpl(
                    indirectColumnMapping.getRelationTable(),
                    srcMappings.toArray(new ColumnToColumnMapping[srcMappings.size()]),
                    dstMappings.toArray(new ColumnToColumnMapping[dstMappings.size()])
                );

                List<FieldToColumnMapping> srcFieldToColumnMappingList = Arrays.asList(ee.getDbMapping().getDbColumnMappings()).stream()
                    .filter(dbColumnMapping -> dbColumnMapping.getColumnType() == ColumnType.SIMPLE)
                    .map(dbColumnMapping -> (SimpleColumnMapping) dbColumnMapping)
                    .map(ff -> new FieldToColumnMapping(ff.getField(), ff.getColumn()))
                    .collect(Collectors.toList());

                List<FieldToColumnMapping> dstFieldToColumnMappingList = Arrays.asList(helper.getDbMapping(indirectColumnMapping.getReferencingEntity()).getDbColumnMappings()).stream()
                    .filter(dbColumnMapping -> dbColumnMapping.getColumnType() == ColumnType.SIMPLE)
                    .map(dbColumnMapping -> (SimpleColumnMapping) dbColumnMapping)
                    .map(ff -> new FieldToColumnMapping(ff.getField(), ff.getColumn()))
                    .collect(Collectors.toList());


                indirectDeleteDependencyListBuilder.add(
                    new IndirectDeleteDependency(field.getName(), new IndirectDeleteHandlerImpl(
                        new RelationTableDeleteFunctionImpl(
                            relationTableDataPopulator,
                            new TableDataPopulatorImpl(
                                ee.getDbMapping().getTable(),
                                ee.getDbMapping().getPrimaryColumn(),
                                srcFieldToColumnMappingList.toArray(new FieldToColumnMapping[srcFieldToColumnMappingList.size()])
                            ),
                            new TableDataPopulatorImpl(
                                helper.getDbMapping(indirectColumnMapping.getReferencingEntity()).getTable(),
                                helper.getDbMapping(indirectColumnMapping.getReferencingEntity()).getPrimaryColumn(),
                                dstFieldToColumnMappingList.toArray(new FieldToColumnMapping[dstFieldToColumnMappingList.size()])
                            )
                        ),
                        deleteFunction(indirectColumnMapping.getReferencingEntity())
                    ))
                );
            }
        }
    }

    private class ManyToOneHandler {
        final Entity ee;
        final ImmutableList.Builder<BelongsToDeleteDependency> belongsToDeleteDependencyListBuilder;
        final ImmutableList.Builder<DirectDeleteDependency> directDeleteDependencyListBuilder;
        final ImmutableList.Builder<IndirectDeleteDependency> indirectDeleteDependencyListBuilder;
        final Map<String, DbColumnMapping> mappingMap;
        final DbColumnMapping mapping;
        final Relationship relationship;

        private ManyToOneHandler(Entity ee, ImmutableList.Builder<BelongsToDeleteDependency> belongsToDeleteDependencyListBuilder, ImmutableList.Builder<DirectDeleteDependency> directDeleteDependencyListBuilder, ImmutableList.Builder<IndirectDeleteDependency> indirectDeleteDependencyListBuilder, Map<String, DbColumnMapping> mappingMap, DbColumnMapping mapping, Relationship relationship) {
            this.ee = ee;
            this.belongsToDeleteDependencyListBuilder = belongsToDeleteDependencyListBuilder;
            this.directDeleteDependencyListBuilder = directDeleteDependencyListBuilder;
            this.indirectDeleteDependencyListBuilder = indirectDeleteDependencyListBuilder;
            this.mappingMap = mappingMap;
            this.mapping = mapping;
            this.relationship = relationship;
        }

        public void handle(Field field) {
            
        }
    }
}
