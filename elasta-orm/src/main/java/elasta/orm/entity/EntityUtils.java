package elasta.orm.entity;

import com.google.common.collect.ImmutableMap;
import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.columnmapping.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-21.
 */
public interface EntityUtils {
    static Map<String, Entity> toEntityNameToEntityMap(Collection<Entity> entities) {
        return ImmutableMap.copyOf(
            entities.stream().collect(
                Collectors.toMap(
                    Entity::getName,
                    e -> e
                )
            )
        );
    }

    static Map<String, Entity> toTableToEntityMap(Collection<Entity> entities) {
        final ImmutableMap.Builder<String, Entity> mapBuilder = ImmutableMap.builder();
        entities.forEach(entity -> mapBuilder.put(entity.getDbMapping().getTable(), entity));
        return mapBuilder.build();
    }

    static TableMapAndDependencyMappingInfo toTableToTableDependencyMap(Collection<Entity> entities) {

        return new TableToTableDependenyMapBuilder().build(entities);
    }

    static ImmutableMap<String, Field> toFieldNameToFieldMap(Field[] fields) {
        ImmutableMap.Builder<String, Field> mapBuilder = ImmutableMap.builder();
        for (Field field : fields) {
            mapBuilder.put(
                field.getName(),
                field
            );
        }
        return mapBuilder.build();
    }

    static Map<String, DbColumnMapping> toFieldToDbColumnMappingMap(DbColumnMapping[] dbColumnMappings) {
        ImmutableMap.Builder<String, DbColumnMapping> mapBuilder = ImmutableMap.builder();

        for (DbColumnMapping dbColumnMapping : dbColumnMappings) {
            mapBuilder.put(
                dbColumnMapping.getField(),
                dbColumnMapping
            );
        }

        return mapBuilder.build();
    }

    static Map<String, SimpleDbColumnMapping> toSimpleDbColumnNameToSimpleDbColumnMapingMap(DbColumnMapping[] dbColumnMappings) {
        ImmutableMap.Builder<String, SimpleDbColumnMapping> mapBuilder = ImmutableMap.builder();
        for (DbColumnMapping dbColumnMapping : dbColumnMappings) {
            if (dbColumnMapping.getColumnType() != ColumnType.SIMPLE) {
                continue;
            }
            SimpleDbColumnMapping mapping = (SimpleDbColumnMapping) dbColumnMapping;
            mapBuilder.put(
                mapping.getColumn(),
                mapping
            );
        }
        return mapBuilder.build();
    }

    class TableToTableDependenyMapBuilder {

        final Map<String, TableDependency> dependencyMap = new HashMap<>();
        final Map<String, Entity> entityNameToEntityMap = new HashMap<>();
        final Map<String, Map<String, Field>> entityToFieldNameToFieldIndexMap = new HashMap<>();

        public TableMapAndDependencyMappingInfo build(Collection<Entity> entities) {
            entities.forEach(entity -> {

                entityNameToEntityMap.put(entity.getName(), entity);

                final DbColumnMapping[] dbColumnMappings = entity.getDbMapping().getDbColumnMappings();

                for (int columnIndex = 0; columnIndex < dbColumnMappings.length; columnIndex++) {

                    final DbColumnMapping dbColumnMapping = dbColumnMappings[columnIndex];

                    if (dbColumnMapping.getColumnType() == ColumnType.SIMPLE) {
                        continue;
                    }

                    switch (dbColumnMapping.getColumnType()) {
                        case DIRECT: {
                            DirectDbColumnMapping mapping = (DirectDbColumnMapping) dbColumnMapping;
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
                            IndirectDbColumnMapping mapping = (IndirectDbColumnMapping) dbColumnMapping;
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
                            VirtualDbColumnMapping mapping = (VirtualDbColumnMapping) dbColumnMapping;
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

            return new TableMapAndDependencyMappingInfo(
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
                    indexMap.put(field.getName(), field);
                }
            }
            return indexMap.get(fieldName);
        }
    }

    class TableMapAndDependencyMappingInfo {
        final Map<String, TableDependency> dependencyMap;
        final Map<String, Entity> entityNameToEntityMap;

        public TableMapAndDependencyMappingInfo(Map<String, TableDependency> dependencyMap, Map<String, Entity> entityNameToEntityMap) {
            Objects.requireNonNull(dependencyMap);
            Objects.requireNonNull(entityNameToEntityMap);
            this.dependencyMap = dependencyMap;
            this.entityNameToEntityMap = entityNameToEntityMap;
        }

        public Map<String, TableDependency> getDependencyMap() {
            return dependencyMap;
        }

        public Map<String, Entity> getEntityNameToEntityMap() {
            return entityNameToEntityMap;
        }
    }
}
