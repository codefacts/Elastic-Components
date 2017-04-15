package elasta.orm.entity.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.DependencyTpl;
import elasta.orm.entity.EntitiesPreprocessor;
import elasta.orm.entity.TableDependency;
import elasta.orm.entity.core.RelationType;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.columnmapping.IndirectRelationMapping;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.entity.core.columnmapping.impl.IndirectRelationMappingImpl;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/17/2017.
 */
final public class EntitiesPreprocessorImpl implements EntitiesPreprocessor {
    @Override
    public List<Entity> process(Params params) {
        return new InternalEntityPreprocessorImpl(params).process();
    }

    final private class InternalEntityPreprocessorImpl {
        private final Collection<Entity> entities;
        private final Map<String, Entity> entityNameToEntityMap;
        private final Map<String, TableDependency> tableToTableDependencyMap;

        public InternalEntityPreprocessorImpl(Params params) {
            entities = params.getEntities();
            entityNameToEntityMap = params.getEntityNameToEntityMap();
            tableToTableDependencyMap = params.getTableToTableDependencyMap();
        }

        public List<Entity> process() {

            ImmutableList.Builder<Entity> listBuilder = ImmutableList.builder();

            entities.stream().map(this::processEntity).forEach(listBuilder::add);

            return listBuilder.build();
        }

        private Entity processEntity(Entity entity) {

            List<RelationMapping> mappingList = Arrays.stream(entity.getDbMapping().getRelationMappings())
                .map(dbColumnMapping -> processColumnMapping(entity, dbColumnMapping))
                .collect(Collectors.toList());

            return new Entity(
                entity.getName(),
                entity.getPrimaryKey(),
                entity.getFields(),
                new DbMapping(
                    entity.getDbMapping().getTable(),
                    entity.getDbMapping().getPrimaryColumn(),
                    entity.getDbMapping().getColumnMappings(),
                    mappingList.toArray(new RelationMapping[mappingList.size()])
                )
            );
        }

        private RelationMapping processColumnMapping(Entity entity, RelationMapping dbColumnMapping) {
            switch (dbColumnMapping.getColumnType()) {
                case INDIRECT:
                    IndirectRelationMapping mapping = (IndirectRelationMapping) dbColumnMapping;
                    Optional<DependencyTpl> dependencyTplOptional = checkCommonRelationalValidity(entity.getDbMapping().getTable(), mapping);

                    return dependencyTplOptional
                        .flatMap(dependencyTpl -> dependencyTpl.getFieldToDependencyInfoMap().values().stream()
                            .filter(dependencyInfo -> {
                                if (dependencyInfo.getRelationMapping().getColumnType() == RelationType.INDIRECT) {

                                    IndirectRelationMapping depMapping = (IndirectRelationMapping) dependencyInfo.getRelationMapping();

                                    if (mapping.getRelationTable().equals(depMapping.getRelationTable())) {
                                        return true;
                                    }
                                }
                                return false;
                            })
                            .findAny()
                            .map(
                                dependencyInfo -> setValuesIfNecessary(
                                    (IndirectRelationMapping) dbColumnMapping,
                                    (IndirectRelationMapping) dependencyInfo.getRelationMapping()
                                ))).orElse(dbColumnMapping);

            }
            return dbColumnMapping;
        }

        private RelationMapping setValuesIfNecessary(IndirectRelationMapping mapping, IndirectRelationMapping mappingOther) {
            if (mapping.getSrcForeignColumnMappingList().size() > 0 && mapping.getDstForeignColumnMappingList().size() > 0) {
                return mapping;
            }

            return new IndirectRelationMappingImpl(
                mapping.getReferencingTable(),
                mapping.getReferencingEntity(),
                mapping.getRelationTable(),
                mapping.getSrcForeignColumnMappingList().size() > 0 ? mapping.getSrcForeignColumnMappingList() : mappingOther.getDstForeignColumnMappingList(),
                mapping.getDstForeignColumnMappingList().size() > 0 ? mapping.getDstForeignColumnMappingList() : mappingOther.getSrcForeignColumnMappingList(),
                mapping.getField()
            );
        }

        private Optional<DependencyTpl> checkCommonRelationalValidity(String table, RelationMapping mapping) {

            TableDependency tableDependency = tableToTableDependencyMap.get(table);

            if (tableDependency == null) {
                return Optional.empty();
            }

            Map<String, DependencyTpl> tableToDependencyInfoMap = tableDependency.getTableToDependencyInfoMap();

            return Optional.of(
                tableToDependencyInfoMap.get(mapping.getReferencingTable())
            );
        }
    }
}
