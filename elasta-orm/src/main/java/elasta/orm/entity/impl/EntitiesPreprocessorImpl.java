package elasta.orm.entity.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.entity.DependencyInfo;
import elasta.orm.entity.DependencyTpl;
import elasta.orm.entity.EntitiesPreprocessor;
import elasta.orm.entity.TableDependency;
import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.ForeignColumnMapping;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.IndirectDbColumnMapping;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.entity.core.columnmapping.impl.IndirectDbColumnMappingImpl;

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
        final Map<String, Entity> entityMap = new HashMap<>();
        private final Collection<Entity> entities;
        private final Map<String, Entity> entityNameToEntityMap;
        private final Map<String, TableDependency> tableToTableDependencyMap;

        public InternalEntityPreprocessorImpl(Params params) {
            entities = params.getEntities();
            entityNameToEntityMap = params.getEntityNameToEntityMap();
            tableToTableDependencyMap = params.getTableToTableDependencyMap();
        }

        public List<Entity> process() {

            entities.forEach(this::processEntity);

            return ImmutableList.copyOf(entityMap.values());
        }

        private Entity processEntity(Entity entity) {
            
            List<DbColumnMapping> mappingList = Arrays.stream(entity.getDbMapping().getDbColumnMappings())
                .map(dbColumnMapping -> processColumnMapping(entity, dbColumnMapping))
                .collect(Collectors.toList());

            return new Entity(
                entity.getName(),
                entity.getPrimaryKey(),
                entity.getFields(),
                new DbMapping(
                    entity.getDbMapping().getTable(),
                    entity.getDbMapping().getPrimaryColumn(),
                    mappingList.toArray(new DbColumnMapping[mappingList.size()])
                )
            );
        }

        private DbColumnMapping processColumnMapping(Entity entity, DbColumnMapping dbColumnMapping) {
            switch (dbColumnMapping.getColumnType()) {
                case INDIRECT:
                    IndirectDbColumnMapping mapping = (IndirectDbColumnMapping) dbColumnMapping;
                    DependencyTpl dependencyTpl = checkCommonRelationalValidity(entity.getDbMapping().getTable(), mapping);

                    return dependencyTpl.getFieldToDependencyInfoMap().values().stream()
                        .filter(dependencyInfo -> {
                            if (dependencyInfo.getDbColumnMapping().getColumnType() == ColumnType.INDIRECT) {

                                IndirectDbColumnMapping depMapping = (IndirectDbColumnMapping) dependencyInfo.getDbColumnMapping();

                                if (mapping.getRelationTable().equals(depMapping.getRelationTable())) {
                                    return true;
                                }
                            }
                            return false;
                        })
                        .findAny()
                        .map(
                            dependencyInfo -> setValuesIfNecessary(
                                (IndirectDbColumnMapping) dbColumnMapping,
                                (IndirectDbColumnMapping) dependencyInfo.getDbColumnMapping()
                            ))
                        .orElse(dbColumnMapping);
            }
            return dbColumnMapping;
        }

        private DbColumnMapping setValuesIfNecessary(IndirectDbColumnMapping mapping, IndirectDbColumnMapping mappingOther) {
            if (mapping.getSrcForeignColumnMappingList().size() > 0 && mapping.getDstForeignColumnMappingList().size() > 0) {
                return mapping;
            }

            return new IndirectDbColumnMappingImpl(
                mapping.getReferencingTable(),
                mapping.getReferencingEntity(),
                mapping.getRelationTable(),
                mapping.getSrcForeignColumnMappingList().size() > 0 ? mapping.getSrcForeignColumnMappingList() : mappingOther.getDstForeignColumnMappingList(),
                mapping.getDstForeignColumnMappingList().size() > 0 ? mapping.getDstForeignColumnMappingList() : mappingOther.getSrcForeignColumnMappingList(),
                mapping.getField()
            );
        }

        private DependencyTpl checkCommonRelationalValidity(String table, RelationMapping mapping) {

            TableDependency tableDependency = tableToTableDependencyMap.get(table);

            Map<String, DependencyTpl> tableToDependencyInfoMap = tableDependency.getTableToDependencyInfoMap();

            return tableToDependencyInfoMap.get(mapping.getReferencingTable());
        }
    }
}
