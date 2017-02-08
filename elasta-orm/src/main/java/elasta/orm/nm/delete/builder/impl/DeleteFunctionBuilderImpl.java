package elasta.orm.nm.delete.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.nm.delete.*;
import elasta.orm.nm.delete.builder.DeleteFunctionBuilder;
import elasta.orm.nm.delete.impl.*;
import elasta.orm.nm.entitymodel.*;
import elasta.orm.nm.entitymodel.columnmapping.DbColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.DirectColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.IndirectColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.SimpleColumnMapping;
import elasta.orm.nm.upsert.ColumnToColumnMapping;
import elasta.orm.nm.upsert.FieldToColumnMapping;
import elasta.orm.nm.upsert.builder.FunctionMap;
import elasta.orm.nm.upsert.builder.impl.UpsertFunctionBuilderImpl;
import elasta.orm.nm.upsert.impl.RelationTableDataPopulatorImpl;
import elasta.orm.nm.upsert.impl.TableDataPopulatorImpl;
import io.vertx.core.json.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

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

        functionMap.putEmpty(entity);

        ImmutableList.Builder<BelongsToDeleteDependency> belongsToDeleteDependencyListBuilder = ImmutableList.builder();
        ImmutableList.Builder<DirectDeleteDependency> directDeleteDependencyListBuilder = ImmutableList.builder();
        ImmutableList.Builder<IndirectDeleteDependency> indirectDeleteDependencyListBuilder = ImmutableList.builder();

        Map<String, DbColumnMapping> mappingMap = helper.getFieldToColumnMappingMap(entity);

        Arrays.asList(ee.getFields()).stream()
            .filter(field -> field.getRelationship().isPresent())
            .forEach(field -> {
                Relationship relationship = field.getRelationship().get();
                final DbColumnMapping mapping = mappingMap.get(field.getName());

                new DependencyHandler(
                    ee,
                    belongsToDeleteDependencyListBuilder,
                    directDeleteDependencyListBuilder,
                    indirectDeleteDependencyListBuilder,
                    mappingMap,
                    mapping,
                    relationship
                ).handle(field);
            });

        final ImmutableList<BelongsToDeleteDependency> belongsToDeleteDependencies = belongsToDeleteDependencyListBuilder.build();
        final ImmutableList<DirectDeleteDependency> directDeleteDependencies = directDeleteDependencyListBuilder.build();
        final ImmutableList<IndirectDeleteDependency> indirectDeleteDependencies = indirectDeleteDependencyListBuilder.build();

        final DeleteFunctionImpl deleteFunction = new DeleteFunctionImpl(
            new DeleteDataPopulatorImpl(
                ee.getDbMapping().getTable(),
                new FieldToColumnMapping[]{
                    new FieldToColumnMapping(ee.getPrimaryKey(), ee.getDbMapping().getPrimaryColumn())
                }
            ),
            directDeleteDependencies.toArray(new DirectDeleteDependency[directDeleteDependencies.size()]),
            indirectDeleteDependencies.toArray(new IndirectDeleteDependency[indirectDeleteDependencies.size()]),
            belongsToDeleteDependencies.toArray(new BelongsToDeleteDependency[belongsToDeleteDependencies.size()])
        );

        functionMap.put(entity, deleteFunction);

        return deleteFunction;
    }

    private DeleteFunction deleteFunction(final String referencingEntity) {

        if (not(functionMap.exists(referencingEntity))) {

            functionMap.put(referencingEntity, new DeleteFunctionBuilderImpl(helper, functionMap).create(referencingEntity));
        }

        return new DeleteFunctionBuilderImpl.DeleteFunctionImpl2(referencingEntity, functionMap);
    }

    private class DependencyHandler {
        final Entity ee;
        final ImmutableList.Builder<BelongsToDeleteDependency> belongsToDeleteDependencyListBuilder;
        final ImmutableList.Builder<DirectDeleteDependency> directDeleteDependencyListBuilder;
        final ImmutableList.Builder<IndirectDeleteDependency> indirectDeleteDependencyListBuilder;
        final Map<String, DbColumnMapping> mappingMap;
        final DbColumnMapping mapping;
        final Relationship relationship;

        private DependencyHandler(Entity ee, ImmutableList.Builder<BelongsToDeleteDependency> belongsToDeleteDependencyListBuilder, ImmutableList.Builder<DirectDeleteDependency> directDeleteDependencyListBuilder, ImmutableList.Builder<IndirectDeleteDependency> indirectDeleteDependencyListBuilder, Map<String, DbColumnMapping> mappingMap, DbColumnMapping mapping, Relationship relationship) {
            this.ee = ee;
            this.belongsToDeleteDependencyListBuilder = belongsToDeleteDependencyListBuilder;
            this.directDeleteDependencyListBuilder = directDeleteDependencyListBuilder;
            this.indirectDeleteDependencyListBuilder = indirectDeleteDependencyListBuilder;
            this.mappingMap = mappingMap;
            this.mapping = mapping;
            this.relationship = relationship;
        }

        public void handle(Field field) {
            switch (mapping.getColumnType()) {
                case VIRTUAL:
                    belongsToDeleteDependencyListBuilder.add(
                        new BelongsToDeleteDependency(new BelongsToDeleteHandlerImpl(deleteFunction(relationship.getEntity())), field.getName())
                    );
                    break;
                case DIRECT:
                    directDeleteDependencyListBuilder.add(
                        directDeleteDependency(field)
                    );
                    break;
                case INDIRECT:
                    indirectDeleteDependencyListBuilder.add(
                        indirectDependency(field)
                    );
                    break;
            }
        }

        private IndirectDeleteDependency indirectDependency(Field field) {

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


            return new IndirectDeleteDependency(field.getName(), new IndirectDeleteHandlerImpl(
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
            ));
        }

        private DirectDeleteDependency directDeleteDependency(Field field) {
            DirectColumnMapping directColumnMapping = (DirectColumnMapping) this.mapping;
            return new DirectDeleteDependency(
                field.getName(),
                new DirectDeleteHandlerImpl(
                    deleteFunction(directColumnMapping.getReferencingEntity())
                )
            );
        }
    }

    final private static class DeleteFunctionImpl2 implements DeleteFunction {
        final String referencingEntity;
        final FunctionMap<DeleteFunction> functionMap;

        public DeleteFunctionImpl2(String referencingEntity, FunctionMap<DeleteFunction> functionMap) {
            this.referencingEntity = referencingEntity;
            this.functionMap = functionMap;
        }

        @Override
        public DeleteData delete(JsonObject entity, DeleteContext context) {
            return functionMap.get(referencingEntity).delete(entity, context);
        }
    }
}
