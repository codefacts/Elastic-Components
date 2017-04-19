package elasta.orm.entity.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.ForeignColumnMapping;
import elasta.orm.entity.core.columnmapping.ColumnMapping;
import elasta.orm.entity.core.columnmapping.DirectRelationMapping;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 4/15/2017.
 */
final class EntityToCombinedColumnsMap {
    final Map<String, Entity> entityNameToEntityMap;
    final Map<String, Set<String>> entityToCombinedColumnsMap;

    EntityToCombinedColumnsMap(Map<String, Entity> entityNameToEntityMap) {
        Objects.requireNonNull(entityNameToEntityMap);
        this.entityNameToEntityMap = entityNameToEntityMap;
        this.entityToCombinedColumnsMap = new HashMap<>();
    }

    boolean exists(String entity, String column) {
        Set<String> combinedColumns = entityToCombinedColumnsMap.get(entity);
        if (combinedColumns == null) {
            entityToCombinedColumnsMap.put(
                entity,
                combinedColumns = combinedColumns(entity)
            );
        }
        return combinedColumns.contains(column);
    }

    private Set<String> combinedColumns(String entity) {

        DbMapping dbMapping = entityNameToEntityMap.get(entity).getDbMapping();

        return ImmutableSet.<String>builder()
            .addAll(columnMappings(dbMapping))
            .addAll(directColumnMappings(dbMapping))
            .build();
    }

    private Set<String> directColumnMappings(DbMapping dbMapping) {
        return Arrays.stream(dbMapping.getRelationMappings())
            .filter(relationMapping -> relationMapping instanceof DirectRelationMapping)
            .map(relationMapping -> (DirectRelationMapping) relationMapping)
            .flatMap(columnMapping -> columnMapping.getForeignColumnMappingList().stream().map(ForeignColumnMapping::getSrcColumn))
            .collect(Collectors.toSet());
    }

    private Set<String> columnMappings(DbMapping dbMapping) {
        return Arrays.stream(dbMapping.getColumnMappings())
            .map(ColumnMapping::getColumn)
            .collect(Collectors.toSet());
    }
}
