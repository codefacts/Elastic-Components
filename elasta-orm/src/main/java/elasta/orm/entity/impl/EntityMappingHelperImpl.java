package elasta.orm.entity.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.entity.*;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import elasta.orm.entity.core.columnmapping.ColumnMapping;
import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.ex.EntityMappingHelperExcpetion;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.PathExpression;

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
    public ColumnMapping[] getColumnMappings(String entity) {
        return getEntity(entity).getDbMapping().getColumnMappings();
    }

    @Override
    public Map<String, Field> getFieldNameToFieldMap(String entity) {
        ImmutableMap.Builder<String, Field> fieldNametoFieldMap = ImmutableMap.builder();
        Arrays.stream(getEntity(entity).getFields())
            .forEach(field -> fieldNametoFieldMap.put(field.getName(), field));

        return fieldNametoFieldMap.build();
    }

    @Override
    public Map<String, ColumnMapping> getColumnNameToColumnMappingMap(String entity) {
        ImmutableMap.Builder<String, ColumnMapping> map = ImmutableMap.builder();
        Arrays.stream(getEntity(entity).getDbMapping().getColumnMappings())
            .map(Utils::<ColumnMapping>cast)
            .forEach(dbColumnMapping -> map.put(dbColumnMapping.getColumn(), dbColumnMapping));
        return map.build();
    }

    @Override
    public Map<String, ColumnMapping> getFieldToColumnMappingMap(String entity) {
        ImmutableMap.Builder<String, ColumnMapping> map = ImmutableMap.builder();
        Arrays.asList(getEntity(entity).getDbMapping().getColumnMappings())
            .forEach(dbColumnMapping -> map.put(dbColumnMapping.getField(), dbColumnMapping));
        return map.build();
    }

    @Override
    public Field getField(String entity, String field) {
        return Arrays.stream(getEntity(entity).getFields())
            .filter(ff -> ff.getName().equals(field))
            .findAny().orElseThrow(() -> new EntityMappingHelperExcpetion("Field '" + field + "' does not exists in '" + entity + "'"));
    }

    @Override
    public Field getFieldByColumn(String entity, String column) {
        return Stream.of(getDbMapping(entity).getColumnMappings())
            .map(dbColumnMapping -> dbColumnMapping)
            .filter(simpleDbColumnMapping -> simpleDbColumnMapping.getColumn().equals(column))
            .map(simpleDbColumnMapping -> getField(entity, simpleDbColumnMapping.getField()))
            .findAny().orElseThrow(() -> new EntityMappingHelperExcpetion("No column found for column '" + column + "' in entity '" + entity + "'"));
    }

    @Override
    public ColumnMapping getColumnMapping(String entity, String field) {
        return Arrays.stream(getEntity(entity).getDbMapping().getColumnMappings())
            .filter(dbColumnMapping -> dbColumnMapping.getField().equals(field))
            .findAny().orElseThrow(() -> new EntityMappingHelperExcpetion("No ColumnMapping found for column '" + entity + "." + field + "'"));
    }

    @Override
    public RelationMapping getRelationMapping(String entity, String field) {
        return Arrays.stream(getEntity(entity).getDbMapping().getRelationMappings())
            .filter(dbColumnMapping -> dbColumnMapping.getField().equals(field))
            .findAny().orElseThrow(() -> new EntityMappingHelperExcpetion("No ColumnMapping found for column '" + entity + "." + field + "'"));
    }

    @Override
    public String getReferencingEntity(String entity, FieldExpression fieldExpression) {
        List<String> parts = fieldExpression.toPathExpression().parts();
        for (int i = 1; i < parts.size(); i++) {
            String part = parts.get(i);
            entity = getRelationMapping(entity, part).getReferencingEntity();
        }
        return entity;
    }

    @Override
    public String getPrimaryKey(String entity) {
        return getEntity(entity).getPrimaryKey();
    }

    @Override
    public ColumnMapping getPrimaryKeyColumnMapping(String entity) {
        return getColumnMapping(entity, getEntity(entity).getPrimaryKey());
    }

    @Override
    public String getPrimaryKeyColumnName(String entity) {
        return Utils.<ColumnMapping>cast(getPrimaryKeyColumnMapping(entity)).getColumn();
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

    @Override
    public boolean isMandatory(String entity, PathExpression pathExpression) {
        List<String> parts = pathExpression.parts();

        for (int i = 1; i < parts.size(); i++) {
            String field = parts.get(i);
            RelationMapping mapping = getRelationMapping(entity, field);

            if (Utils.not(mapping.getOptions().isMandatory())) {
                return false;
            }

            entity = mapping.getReferencingEntity();
        }

        return true;
    }

    @Override
    public List<RelationMapping> getRelationMappings(String entity) {
        return Arrays.asList(getDbMapping(entity).getRelationMappings());
    }
}
