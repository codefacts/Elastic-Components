package elasta.orm.entity.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.entity.*;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import elasta.orm.entity.core.columnmapping.SimpleDbColumnMapping;
import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.ex.EntityMappingHelperExcpetion;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Jango on 2017-01-21.
 */
final public class EntityMappingHelperImpl implements EntityMappingHelper {
    final Map<String, Entity> entityMap;
    final Map<String, Entity> tableToEntityMap;

    public EntityMappingHelperImpl(Map<String, Entity> entityMap) {
        Objects.requireNonNull(entityMap);
        this.entityMap = entityMap;
        this.tableToEntityMap = ImmutableMap.copyOf(
            entityMap.entrySet().stream()
                .collect(
                    Collectors.toMap(
                        e -> e.getValue().getDbMapping().getTable(),
                        Map.Entry::getValue
                    )
                )
        );
    }

    public EntityMappingHelperImpl(Map<String, Entity> entityMap, Map<String, Entity> tableToEntityMap) {
        Objects.requireNonNull(entityMap);
        Objects.requireNonNull(tableToEntityMap);
        this.entityMap = entityMap;
        this.tableToEntityMap = tableToEntityMap;
    }

    @Override
    public Entity getEntity(String entity) {
        Entity ee = entityMap.get(entity);
        if (ee == null) {
            throw new NullPointerException("No Entity found for key '" + entity + "'");
        }
        return ee;
    }

    @Override
    public Entity getEntityByTable(String table) {
        Entity entity = tableToEntityMap.get(table);
        if (entity == null) {
            throw new NullPointerException("No Entity found for dependentTable '" + table + "'");
        }
        return entity;
    }

    @Override
    public Field[] getFields(String entity) {
        return getEntity(entity).getFields();
    }

    @Override
    public DbMapping getDbMapping(String entity) {
        return getEntity(entity).getDbMapping();
    }

    @Override
    public DbColumnMapping[] getColumnMappings(String entity) {
        return getEntity(entity).getDbMapping().getDbColumnMappings();
    }

    @Override
    public Map<String, Field> getFieldNameToFieldMap(String entity) {
        ImmutableMap.Builder<String, Field> fieldNametoFieldMap = ImmutableMap.builder();
        Arrays.asList(getEntity(entity).getFields()).stream()
            .forEach(field -> fieldNametoFieldMap.put(field.getName(), field));

        return fieldNametoFieldMap.build();
    }

    @Override
    public Map<String, SimpleDbColumnMapping> getColumnNameToColumnMappingMap(String entity) {
        ImmutableMap.Builder<String, SimpleDbColumnMapping> map = ImmutableMap.builder();
        Arrays.asList(getEntity(entity).getDbMapping().getDbColumnMappings())
            .stream()
            .filter(dbColumnMapping -> dbColumnMapping.getColumnType() == ColumnType.SIMPLE)
            .map(Utils::<SimpleDbColumnMapping>cast)
            .forEach(dbColumnMapping -> map.put(dbColumnMapping.getColumn(), dbColumnMapping));
        return map.build();
    }

    @Override
    public Map<String, DbColumnMapping> getFieldToColumnMappingMap(String entity) {
        ImmutableMap.Builder<String, DbColumnMapping> map = ImmutableMap.builder();
        Arrays.asList(getEntity(entity).getDbMapping().getDbColumnMappings())
            .forEach(dbColumnMapping -> map.put(dbColumnMapping.getField(), dbColumnMapping));
        return map.build();
    }

    @Override
    public Field getField(String entity, String field) {
        return Arrays.asList(getEntity(entity).getFields())
            .stream()
            .filter(ff -> ff.getName().equals(field))
            .findAny().orElseThrow(() -> new EntityMappingHelperExcpetion("Field '" + field + "' does not exists in '" + entity + "'"));
    }

    @Override
    public Field getFieldByColumn(String entity, String column) {
        return Stream.of(getDbMapping(entity).getDbColumnMappings())
            .filter(dbColumnMapping -> dbColumnMapping.getColumnType() == ColumnType.SIMPLE)
            .map(dbColumnMapping -> (SimpleDbColumnMapping) dbColumnMapping)
            .filter(simpleDbColumnMapping -> simpleDbColumnMapping.getColumn().equals(column))
            .map(simpleDbColumnMapping -> getField(entity, simpleDbColumnMapping.getField()))
            .findAny().orElseThrow(() -> new EntityMappingHelperExcpetion("No field found for column '" + column + "' in entity '" + entity + "'"));
    }

    @Override
    public DbColumnMapping getColumnMapping(String entity, String field) {
        return Arrays.stream(getEntity(entity).getDbMapping().getDbColumnMappings())
            .filter(dbColumnMapping -> dbColumnMapping.getField().equals(field))
            .findAny().get();
    }

    @Override
    public String getPrimaryKey(String entity) {
        return getEntity(entity).getPrimaryKey();
    }

    @Override
    public SimpleDbColumnMapping getPrimaryKeyColumnMapping(String entity) {
        return (SimpleDbColumnMapping) getColumnMapping(entity, getEntity(entity).getPrimaryKey());
    }

    @Override
    public String getPrimaryKeyColumnName(String entity) {
        return Utils.<SimpleDbColumnMapping>cast(getPrimaryKeyColumnMapping(entity)).getColumn();
    }

    @Override
    public boolean exists(String entity) {
        return entityMap.containsKey(entity);
    }

    @Override
    public String getTable(String entity) {
        return getEntity(entity).getDbMapping().getTable();
    }

    @Override
    public DbMapping getDbMappingByTable(String table) {
        return getEntityByTable(table).getDbMapping();
    }

    @Override
    public List<String> getTables() {
        return ImmutableList.copyOf(
            tableToEntityMap.keySet()
        );
    }

    @Override
    public List<Entity> getEntities() {
        return ImmutableList.copyOf(
            entityMap.values()
        );
    }
}
