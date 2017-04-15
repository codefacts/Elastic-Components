package elasta.orm.entity;

import com.google.common.collect.ImmutableMap;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.columnmapping.*;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
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

    static Map<String, ColumnMapping> toFieldToColumnMappingMap(ColumnMapping[] dbColumnMappings) {
        ImmutableMap.Builder<String, ColumnMapping> mapBuilder = ImmutableMap.builder();

        for (ColumnMapping dbColumnMapping : dbColumnMappings) {
            mapBuilder.put(
                dbColumnMapping.getField(),
                dbColumnMapping
            );
        }

        return mapBuilder.build();
    }

    static Map<String, RelationMapping> toFieldToRelationMappingMap(RelationMapping[] relationMappings) {
        ImmutableMap.Builder<String, RelationMapping> mapBuilder = ImmutableMap.builder();

        for (RelationMapping relationMapping : relationMappings) {
            mapBuilder.put(
                relationMapping.getField(),
                relationMapping
            );
        }

        return mapBuilder.build();
    }

    static Map<String, ColumnMapping> toColumnNameToColumnMapingMap(ColumnMapping[] columnMappings) {
        ImmutableMap.Builder<String, ColumnMapping> mapBuilder = ImmutableMap.builder();
        for (ColumnMapping columnMapping : columnMappings) {
            mapBuilder.put(
                columnMapping.getColumn(),
                columnMapping
            );
        }
        return mapBuilder.build();
    }

    @Value
    @Builder
    class TableMapAndDependencyMappingInfo {
        final Map<String, TableDependency> tableToTableDependencyMap;
        final Map<String, Entity> entityNameToEntityMap;

        public TableMapAndDependencyMappingInfo(Map<String, TableDependency> tableToTableDependencyMap, Map<String, Entity> entityNameToEntityMap) {
            Objects.requireNonNull(tableToTableDependencyMap);
            Objects.requireNonNull(entityNameToEntityMap);
            this.tableToTableDependencyMap = ImmutableMap.copyOf(tableToTableDependencyMap);
            this.entityNameToEntityMap = ImmutableMap.copyOf(entityNameToEntityMap);
        }
    }
}
