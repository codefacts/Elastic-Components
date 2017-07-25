package elasta.orm.entity.impl;

import elasta.orm.entity.DependencyInfo;
import elasta.orm.entity.EntityUtils;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.columnmapping.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 4/14/2017.
 */
public class TableToTableDependenyMapBuilder {

    final Map<String, TableDependency> dependencyMap = new HashMap<>();
    final Map<String, Entity> entityNameToEntityMap = new HashMap<>();
    final Map<String, Map<String, Field>> entityToFieldNameToFieldIndexMap = new HashMap<>();

    public EntityUtils.TableMapAndDependencyMappingInfo build(Collection<Entity> entities) {
        entities.forEach(entity -> {

            entityNameToEntityMap.put(entity.getName(), entity);

            final RelationMapping[] relationMappings = entity.getDbMapping().getRelationMappings();

            for (int columnIndex = 0; columnIndex < relationMappings.length; columnIndex++) {

                final RelationMapping dbColumnMapping = relationMappings[columnIndex];

                switch (dbColumnMapping.getColumnType()) {
                    case DIRECT: {
                        DirectRelationMapping mapping = (DirectRelationMapping) dbColumnMapping;
                        putInTable(
                            mapping.getReferencingTable(),
                            entity,
                            new DependencyInfo(
                                getField(entity, mapping.getField()),
                                mapping
                            )
                        );
                    }
                    break;
                    case INDIRECT: {
                        IndirectRelationMapping mapping = (IndirectRelationMapping) dbColumnMapping;
                        putInTable(
                            mapping.getReferencingTable(),
                            entity,
                            new DependencyInfo(
                                getField(entity, mapping.getField()),
                                mapping
                            )
                        );
                    }
                    break;
                    case VIRTUAL: {
                        VirtualRelationMapping mapping = (VirtualRelationMapping) dbColumnMapping;
                        putInTable(
                            mapping.getReferencingTable(),
                            entity,
                            new DependencyInfo(
                                getField(entity, mapping.getField()),
                                mapping
                            )
                        );
                    }
                    break;
                }
            }
        });

        return new EntityUtils.TableMapAndDependencyMappingInfo(
            dependencyMap,
            entityNameToEntityMap
        );
    }

    private void putInTable(String referencingTable, Entity entity, DependencyInfo dependencyInfo) {
        TableDependency tableDependency = dependencyMap.get(referencingTable);
        if (tableDependency == null) {
            dependencyMap.put(
                referencingTable,
                tableDependency = new TableDependency(
                    new HashMap<>()
                )
            );
        }
        tableDependency.add(
            entity,
            dependencyInfo
        );
    }

    private Field getField(Entity entity, String fieldName) {
        String entityName = entity.getName();
        Map<String, Field> indexMap = entityToFieldNameToFieldIndexMap.get(entityName);
        if (indexMap == null) {
            entityToFieldNameToFieldIndexMap.put(
                entityName,
                indexMap = new HashMap<>()
            );

            final Field[] fields = entity.getFields();
            for (Field field : fields) {
                Objects.requireNonNull(field, "Null in fields in entity '" + entityName + "'");
                indexMap.put(field.getName(), field);
            }
        }
        return indexMap.get(fieldName);
    }
}
