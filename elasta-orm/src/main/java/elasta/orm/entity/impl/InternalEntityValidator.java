package elasta.orm.entity.impl;

import elasta.commons.Utils;
import elasta.orm.entity.DependencyTpl;
import elasta.orm.entity.EntityUtils;
import elasta.orm.entity.EntityValidator;
import elasta.orm.entity.TableDependency;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.entity.ex.EntityValidationException;
import elasta.orm.entity.ex.InternalEntityValidatorException;

import java.util.*;

/**
 * Created by sohan on 4/14/2017.
 */
final class InternalEntityValidator {
    final Entity entity;
    final Map<String, Field> fieldNameToFieldMap;
    final Map<String, ColumnMapping> fieldToColumnMappingMap;
    final Map<String, RelationMapping> fieldToRelationMappingMap;
    final Map<String, TableDependency> tableToTableDependencyMap;
    final Map<String, Entity> entityNameToEntityMap;
    final EntityToCombinedColumnsMap entityToCombinedColumnsMap;

    public InternalEntityValidator(EntityValidator.Params params) {
        Objects.requireNonNull(params);
        this.entity = params.getEntity();
        this.tableToTableDependencyMap = params.getTableToTabledependencyMap();
        this.entityNameToEntityMap = params.getEntityNameToEntityMap();
        this.fieldNameToFieldMap = EntityUtils.toFieldNameToFieldMap(params.getEntity().getFields());
        fieldToColumnMappingMap = EntityUtils.toFieldToColumnMappingMap(
            entity.getDbMapping().getColumnMappings()
        );
        fieldToRelationMappingMap = EntityUtils.toFieldToRelationMappingMap(
            entity.getDbMapping().getRelationMappings()
        );
        entityToCombinedColumnsMap = new EntityToCombinedColumnsMap(
            entityNameToEntityMap
        );
    }

    public void validate() {
        validatePrimaryKey();

        for (Field field : entity.getFields()) {
            validateField(field);
        }
    }

    private void validateField(Field field) {

        if (Utils.not(fieldToColumnMappingMap.containsKey(field.getName()) || fieldToRelationMappingMap.containsKey(field.getName()))) {
            throw new EntityValidationException("No DbColumnMapping found for field '" + field.getName() + "'");
        }

        if (Utils.not(fieldToRelationMappingMap.containsKey(field.getName()))) {

            ColumnMapping mapping = fieldToColumnMappingMap.get(field.getName());
            final boolean typeIsOk = field.getJavaType() != JavaType.OBJECT && field.getJavaType() != JavaType.ARRAY;
            if (Utils.not(typeIsOk)) {
                throw new EntityValidationException("Type '" + field.getJavaType() + "' of Field '" + field.getName() + "' is not supported for mapping '" + ColumnMapping.class.getSimpleName() + "'");
            }
            if (field.getRelationship().isPresent()) {
                throw new EntityValidationException("Field '" + field.getName() + "' can not have relationship '" + field.getRelationship() + "' with ColumnMapping '" + mapping + "'");
            }
            return;
        }

        RelationMapping relationMapping = fieldToRelationMappingMap.get(field.getName());

        switch (relationMapping.getColumnType()) {

            case INDIRECT: {

                new IndirectColumnMappingValidator(
                    this, tableToTableDependencyMap, entityNameToEntityMap, entity,
                    field,
                    (IndirectRelationMapping) relationMapping
                ).validate();
            }
            break;
            case VIRTUAL: {
                new VirtualColumnValidator(
                    this, entity,
                    field,
                    (VirtualRelationMapping) relationMapping
                ).validate();
            }
            break;
            case DIRECT: {
                new DirectColumnValidator(
                    this, entity,
                    field,
                    (DirectRelationMapping) relationMapping
                ).validate();
            }
            break;
        }
    }

    boolean columnExistsInCombinedColumns(String entity, String column) {
        return entityToCombinedColumnsMap.exists(entity, column);
    }

    private Entity getEntityByName(String entityName) {
        Entity entity = entityNameToEntityMap.get(entityName);
        if (entity == null) {
            throw new InternalEntityValidatorException("No entity found for entity name '" + entityName + "'");
        }
        return entity;
    }

    Optional<DependencyTpl> checkRelationalValidity(RelationMapping mapping) {

        Optional<DependencyTpl> dependencyTplOptional = getDependencyTpl(entity, mapping);

        return dependencyTplOptional.map(dependencyTpl -> {

            boolean mappingEntityInfoIsCorrect = mapping.getReferencingTable().equals(dependencyTpl.getEntity().getDbMapping().getTable())
                && mapping.getReferencingEntity().equals(dependencyTpl.getEntity().getName());

            if (Utils.not(mappingEntityInfoIsCorrect)) {
                throw new EntityValidationException("Referencing Entity '" + mapping.getReferencingEntity() + "' or Table '" + mapping.getReferencingTable() + "' does not match with actual Referencing Entity '" + dependencyTpl.getEntity().getName() + "' or Table '" + dependencyTpl.getEntity().getDbMapping().getTable() + "'");
            }

            return dependencyTpl;
        });
    }

    boolean isEqualDirectAndVirtual(List<ForeignColumnMapping> directForeignColumnMappingList, List<ForeignColumnMapping> virtualForeignColumnMappingList) {

        if (directForeignColumnMappingList == virtualForeignColumnMappingList) {
            return true;
        }

        if (directForeignColumnMappingList.size() != virtualForeignColumnMappingList.size()) {
            return false;
        }

        for (int i = 0; i < directForeignColumnMappingList.size(); i++) {
            ForeignColumnMapping mapping1 = directForeignColumnMappingList.get(i);
            ForeignColumnMapping mapping2 = virtualForeignColumnMappingList.get(i);

            boolean equals = mapping1
                .getSrcColumn().equals(
                    mapping2.getSrcColumn()
                )
                &&
                mapping1.getDstColumn().equals(
                    mapping2.getDstColumn()
                );

            if (Utils.not(equals)) {
                return false;
            }
        }
        return true;
    }

    boolean isEqualBothSide(List<ForeignColumnMapping> srcForeignColumnMappingList, List<ForeignColumnMapping> dstForeignColumnMappingList) {
        if (srcForeignColumnMappingList == dstForeignColumnMappingList) {
            return true;
        }
        if (srcForeignColumnMappingList.size() != dstForeignColumnMappingList.size()) {
            return false;
        }
        for (int i = 0; i < srcForeignColumnMappingList.size(); i++) {
            ForeignColumnMapping srcMapping = srcForeignColumnMappingList.get(i);
            ForeignColumnMapping dstMapping = dstForeignColumnMappingList.get(i);

            boolean equalBoth = isEqualBoth(srcMapping, dstMapping);

            if (Utils.not(equalBoth)) {
                return false;
            }
        }
        return true;
    }

    private boolean isEqualBoth(ForeignColumnMapping srcMapping, ForeignColumnMapping dstMapping) {
        return srcMapping.getSrcColumn().equals(
            dstMapping.getSrcColumn()
        ) && srcMapping.getDstColumn().equals(
            dstMapping.getDstColumn()
        );
    }

    private Optional<DependencyTpl> getDependencyTpl(Entity entity, RelationMapping mapping) {
        TableDependency tableDependency = tableToTableDependencyMap.get(entity.getDbMapping().getTable());
        if (tableDependency == null) {
            return Optional.empty();
//                throw new EntityValidationException("No TableDependency found for table '" + entity.getDbMapping().getReferencingTable() + " that has a mapping '" + mapping + "'");
        }
        Map<String, DependencyTpl> tableToDependencyInfoMap = tableDependency.getTableToDependencyInfoMap();

        DependencyTpl dependencyTpl = tableToDependencyInfoMap.get(mapping.getReferencingTable());
        if (dependencyTpl == null) {
            throw new EntityValidationException(
                "No dependency found for referencingTable '" + mapping.getReferencingTable() + "' in relationship '" + entity.getName() + "." + mapping.getField() + "' - '" + mapping.getReferencingEntity() + "'"
            );
        }
        return Optional.of(dependencyTpl);
    }

    private void validatePrimaryKey() {
        boolean containsKey = fieldNameToFieldMap.containsKey(entity.getPrimaryKey());
        if (Utils.not(containsKey)) {
            throw new EntityValidationException("Fields of Entity '" + entity.getName() + "' does not contains primaryKey '" + entity.getPrimaryKey() + "'");
        }
        Field field = fieldNameToFieldMap.get(entity.getPrimaryKey());

        boolean typeIsOk = field.getJavaType() != JavaType.OBJECT
            && field.getJavaType() != JavaType.ARRAY;
        if (Utils.not(typeIsOk)) {
            throw new EntityValidationException("Primary column type '" + field.getJavaType() + "' is unsupported");
        }

        boolean containsDbColumn = fieldToColumnMappingMap.containsKey(field.getName());
        if (Utils.not(containsDbColumn)) {
            throw new EntityValidationException("No DbColumnMapping found for primary key '" + entity.getPrimaryKey() + "'");
        }
    }


    void checkFieldTypeAndName(Relationship relationship, Field field) {
        if (Utils.not(
            (
                field.getJavaType() == JavaType.OBJECT
                    && relationship.getName() == Relationship.Name.HAS_ONE
            ) || (
                field.getJavaType() == JavaType.ARRAY
                    && relationship.getName() == Relationship.Name.HAS_MANY
            )
        )) {
            throw new EntityValidationException(
                "javaType '" + field.getJavaType() + "' is not valid for relationship name '" + relationship.getName() + "' in field '" + field.getName() + "'"
            );
        }
    }
}
