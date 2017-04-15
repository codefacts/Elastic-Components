package elasta.orm.entity.impl;

import elasta.commons.Utils;
import elasta.orm.entity.DependencyInfo;
import elasta.orm.entity.DependencyTpl;
import elasta.orm.entity.EntityUtils;
import elasta.orm.entity.TableDependency;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.IndirectRelationMapping;
import elasta.orm.entity.core.columnmapping.ColumnMapping;
import elasta.orm.entity.ex.EntityValidationException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 4/14/2017.
 */
final class IndirectColumnMappingValidator {
    private final InternalEntityValidator internalEntityValidator;
    private final Map<String, TableDependency> tableToTableDependencyMap;
    private final Map<String, Entity> entityNameToEntityMap;
    final Entity entity;
    final Field field;
    final IndirectRelationMapping mapping;

    public IndirectColumnMappingValidator(InternalEntityValidator internalEntityValidator, Map<String, TableDependency> tableToTableDependencyMap, Map<String, Entity> entityNameToEntityMap, Entity entity, Field field, IndirectRelationMapping mapping) {
        Objects.requireNonNull(internalEntityValidator);
        Objects.requireNonNull(tableToTableDependencyMap);
        Objects.requireNonNull(entityNameToEntityMap);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(field);
        Objects.requireNonNull(mapping);
        this.tableToTableDependencyMap = tableToTableDependencyMap;
        this.entityNameToEntityMap = entityNameToEntityMap;
        this.entity = entity;
        this.field = field;
        this.mapping = mapping;
        this.internalEntityValidator = internalEntityValidator;
    }

    public void validate() {

        checkFieldType();

        Relationship relationship = field.getRelationship().orElseThrow(() -> new EntityValidationException("No relationship present for mapping '" + mapping + "' in table '" + entity.getDbMapping().getTable() + "'"));

        internalEntityValidator.checkFieldTypeAndName(relationship, field);

        if (Utils.not(tableToTableDependencyMap.containsKey(entity.getDbMapping().getTable()))) {

            final Entity referencingEntity = entityNameToEntityMap.get(mapping.getReferencingEntity());

            checkRelationshipToReferencingEntity(referencingEntity);

            checkAllSrcColumnsExistsInCombinedColumns(entity.getName(), mapping.getSrcForeignColumnMappingList());
            checkAllSrcColumnsExistsInCombinedColumns(referencingEntity.getName(), mapping.getDstForeignColumnMappingList());

            return;
        }

        Optional<DependencyTpl> dependencyTplOptional = internalEntityValidator.checkRelationalValidity(mapping);

        Optional<DependencyInfo> info = dependencyTplOptional.flatMap(dependencyTpl -> dependencyTpl.getFieldToDependencyInfoMap().values().stream()
            .filter(this::findDependencyInfoInOppositeSide)
            .findAny());

        if (info.isPresent()) {

            DependencyInfo dependencyInfo = info.get();

            checkMappingValidity(
                mapping,
                (IndirectRelationMapping) dependencyInfo.getRelationMapping(),
                dependencyTplOptional.get(),
                dependencyInfo
            );

        } else {
            checkAllSrcColumnsExistsInCombinedColumns(
                entity.getName(), mapping.getSrcForeignColumnMappingList()
            );
            checkAllSrcColumnsExistsInCombinedColumns(
                entityNameToEntityMap.get(mapping.getReferencingEntity()).getName(), mapping.getDstForeignColumnMappingList()
            );
        }

        info.ifPresent(dependencyInfo -> {
            Relationship relationshipOther = dependencyInfo.getField().getRelationship().orElseThrow(() -> new EntityValidationException("No relationship present for mapping '" + mapping + "' in table '" + entity.getDbMapping().getTable() + "'"));

            if (Utils.not(
                (relationship.getType() == Relationship.Type.ONE_TO_ONE
                    && relationshipOther.getType() == Relationship.Type.ONE_TO_ONE)
                    || (relationship.getType() == Relationship.Type.ONE_TO_MANY
                    && relationshipOther.getType() == Relationship.Type.MANY_TO_ONE)
                    || (relationship.getType() == Relationship.Type.MANY_TO_ONE
                    && relationshipOther.getType() == Relationship.Type.ONE_TO_MANY)
                    || (relationship.getType() == Relationship.Type.MANY_TO_MANY
                    && relationshipOther.getType() == Relationship.Type.MANY_TO_MANY)
            )) {
                throw new EntityValidationException(
                    "Relationship type '" + relationship.getType() + "' is invalid for mapping type '" + mapping.getColumnType() + "'"
                );
            }
        });

    }

    private boolean findDependencyInfoInOppositeSide(DependencyInfo dependencyInfo) {
        if (dependencyInfo.getRelationMapping().getColumnType() == RelationType.INDIRECT) {

            IndirectRelationMapping depMapping = (IndirectRelationMapping) dependencyInfo.getRelationMapping();

            if (mapping.getRelationTable().equals(depMapping.getRelationTable())) {
                return true;
            }
        }
        return false;
    }

    private void checkRelationshipToReferencingEntity(Entity referencingEntity) {

        if (referencingEntity == null) {
            throw new EntityValidationException(
                "No entity found for referenceEntity '" + mapping.getReferencingEntity() + "' in mapping '" + mapping + "' in relationship '" + entity.getName() + "' <-> '" + mapping.getReferencingEntity() + "'"
            );
        }
        if (Utils.not(
            mapping.getReferencingEntity().equals(
                referencingEntity.getName()
            ) && mapping.getReferencingTable().equals(
                referencingEntity.getDbMapping().getTable()
            )
        )) {
            throw new EntityValidationException("Referencing Entity '" + mapping.getReferencingEntity() + "' or Table '" + mapping.getReferencingTable() + "' does not match with actual Referencing Entity '" + referencingEntity.getName() + "' or Table '" + referencingEntity.getDbMapping().getTable() + "'");
        }
    }

    private void checkFieldType() {

        boolean isFieldTypeOk = field.getJavaType() == JavaType.OBJECT || field.getJavaType() == JavaType.ARRAY;
        if (Utils.not(isFieldTypeOk)) {
            throw new EntityValidationException("Field '" + field.getName() + "' has an invalid type '" + field.getJavaType() + "' for dbColumnMappingType '"
                + mapping.getColumnType() + "'");
        }
    }

    private void checkMappingValidity(IndirectRelationMapping mapping, IndirectRelationMapping depMapping, DependencyTpl dependencyTpl, DependencyInfo dependencyInfo) {
        List<ForeignColumnMapping> srcForeignColumnMappingList = mapping.getSrcForeignColumnMappingList();
        List<ForeignColumnMapping> dstForeignColumnMappingList = mapping.getDstForeignColumnMappingList();
        List<ForeignColumnMapping> srcForeignColumnMappingList1 = depMapping.getSrcForeignColumnMappingList();
        List<ForeignColumnMapping> dstForeignColumnMappingList1 = depMapping.getDstForeignColumnMappingList();

        if (srcForeignColumnMappingList.size() > 0
            && dstForeignColumnMappingList1.size() > 0
            && Utils.not(
            internalEntityValidator.isEqualBothSide(srcForeignColumnMappingList, dstForeignColumnMappingList1)
        )) {
            throw new EntityValidationException("mapping.srcForeignColumnMappingList '" +
                srcForeignColumnMappingList
                + "' and depMapping.dstForeignColumnMappingList '" +
                dstForeignColumnMappingList1
                + "' does not match in relationship '" + entity.getName() + "." + field.getName() + "' <-> '" + dependencyTpl.getEntity().getName() + "." + dependencyInfo.getField().getName() + "'");
        }

        if (srcForeignColumnMappingList1.size() > 0
            && dstForeignColumnMappingList.size() > 0
            && Utils.not(
            internalEntityValidator.isEqualBothSide(srcForeignColumnMappingList1, dstForeignColumnMappingList)
        )) {
            throw new EntityValidationException("mapping.srcForeignColumnMappingList '" +
                srcForeignColumnMappingList
                + "' and depMapping.dstForeignColumnMappingList '" +
                dstForeignColumnMappingList1
                + "' does not match in relationship '" + entity.getName() + "." + field.getName() + "' <-> '" + dependencyTpl.getEntity().getName() + "." + dependencyInfo.getField().getName() + "'");
        }

        if (srcForeignColumnMappingList.isEmpty() && dstForeignColumnMappingList1.isEmpty()) {
            throw new EntityValidationException(
                "Both srcForeignColumnMappingList and dstForeignColumnMappingList is empty in relationship '" + entity.getName() + "." + field.getName() + "' <-> '" + dependencyTpl.getEntity().getName() + "." + dependencyInfo.getField().getName() + "'"
            );
        }

        if (dstForeignColumnMappingList.isEmpty() && srcForeignColumnMappingList1.isEmpty()) {
            throw new EntityValidationException(
                "Both srcForeignColumnMappingList and dstForeignColumnMappingList is empty in relationship '" + entity.getName() + "." + field.getName() + "' <-> '" + dependencyTpl.getEntity().getName() + "." + dependencyInfo.getField().getName() + "'"
            );
        }

        checkAllSrcColumnsExistsInCombinedColumns(
            entity.getName(),
            srcForeignColumnMappingList.size() > 0 ? srcForeignColumnMappingList : dstForeignColumnMappingList1
        );

        checkAllSrcColumnsExistsInCombinedColumns(
            dependencyTpl.getEntity().getName(),
            dstForeignColumnMappingList.size() > 0 ? dstForeignColumnMappingList : srcForeignColumnMappingList1
        );
    }

    private void checkAllSrcColumnsExistsInCombinedColumns(String entity, List<ForeignColumnMapping> foreignColumnMappings) {

        foreignColumnMappings.forEach(foreignColumnMapping -> {

            boolean existsInCombinedColumns = internalEntityValidator.columnExistsInCombinedColumns(entity, foreignColumnMapping.getSrcColumn());

            if (Utils.not(existsInCombinedColumns)) {
                throw new EntityValidationException(
                    "Mapping column '" + foreignColumnMapping.getSrcColumn() + "' does not exists in table column list in table '" + entityNameToEntityMap.get(entity).getDbMapping().getTable() + "'"
                );
            }
        });
    }
}
