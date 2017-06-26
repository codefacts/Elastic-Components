package elasta.orm.entity.impl;

import elasta.commons.Utils;
import elasta.orm.entity.DependencyInfo;
import elasta.orm.entity.DependencyTpl;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.DirectRelationMapping;
import elasta.orm.entity.core.columnmapping.VirtualRelationMapping;
import elasta.orm.entity.ex.EntityValidationException;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 4/14/2017.
 */
final class VirtualColumnValidator {
    final InternalEntityValidator internalEntityValidator;
    final Entity entity;
    final Field field;
    final VirtualRelationMapping mapping;

    public VirtualColumnValidator(InternalEntityValidator internalEntityValidator, Entity entity, Field field, VirtualRelationMapping mapping) {
        Objects.requireNonNull(internalEntityValidator);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(field);
        Objects.requireNonNull(mapping);
        this.internalEntityValidator = internalEntityValidator;
        this.entity = entity;
        this.field = field;
        this.mapping = mapping;
    }

    public void validate() {

        checkFieldType();

        Relationship relationship = field.getRelationship()
            .orElseThrow(() -> new EntityValidationException("No relationship present for mapping '" + mapping + "' in table '" + entity.getDbMapping().getTable() + "'"));

        internalEntityValidator.checkFieldTypeAndName(relationship, field);

        checkRelationshipType(relationship);

        Optional<DependencyTpl> dependencyTplOptional = internalEntityValidator.checkRelationalValidity(mapping);

        dependencyTplOptional
            .flatMap(dependencyTpl -> dependencyTpl.getFieldToDependencyInfoMap().values().stream()
                .filter(this::findDependencyInfoInOppositeSide)
                .findAny()
            )
//            .orElseThrow(() -> new EntityValidationException("No Mapping found in the opposite side for mapping '" + mapping + "' in relationship '" + entity.getName() + "." + column.getName() + "' <- '" + dependencyTplOptional.map(dependencyTpl -> dependencyTpl.getEntity().getName()).orElse("") + "'"));
            .ifPresent(dependencyInfo -> {

                DependencyTpl dependencyTpl = dependencyTplOptional.get();

                Relationship relationshipOther = dependencyInfo.getField().getRelationship().orElseThrow(() -> new EntityValidationException("No relationship present for mapping '" + mapping + "' in table '" + dependencyTpl.getEntity().getDbMapping().getTable() + "'"));

                checkRelationshipAndJavaType(dependencyInfo, dependencyTpl, relationship, relationshipOther);

            });

        checkAllForeignColumnExistsInOppositeEntityDbMapping();
    }

    private boolean findDependencyInfoInOppositeSide(DependencyInfo dependencyInfo) {
        if (dependencyInfo.getRelationMapping().getColumnType() == RelationType.DIRECT) {

            DirectRelationMapping depMapping = (DirectRelationMapping) dependencyInfo.getRelationMapping();

//                        if (mapping.getForeignColumnMappingList().size() != depMapping.getForeignColumnMappingList().size()) {
//                            throw new EntityValidationException(
//                                "mapping.getForeignColumnMappingList().size() != depMapping.getForeignColumnMappingList().size() in relationship '" + entity.getName() + "." + column.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "'"
//                            );
//                        }

            return internalEntityValidator.isEqualDirectAndVirtual(
                depMapping.getForeignColumnMappingList(), mapping.getForeignColumnMappingList()
            );
        }

        return false;
    }

    private void checkRelationshipType(Relationship relationship) {

        if (Utils.not(relationship.getType() == Relationship.Type.ONE_TO_ONE || relationship.getType() == Relationship.Type.ONE_TO_MANY)) {
            throw new EntityValidationException(
                "Relationship type '" + relationship.getType() + "' is invalid for mapping type '" + mapping.getColumnType() + "'"
            );
        }
    }

    private void checkRelationshipAndJavaType(DependencyInfo dependencyInfo, DependencyTpl dependencyTpl, Relationship relationship, Relationship relationshipOther) {

        if (Utils.not(relationship.getType() == Relationship.Type.ONE_TO_ONE || relationship.getType() == Relationship.Type.ONE_TO_MANY)) {
            throw new EntityValidationException(
                "Relationship type '" + relationship.getType() + "' is invalid for mapping type '" + mapping.getColumnType() + "' in relationship '" +
                    relationship.getEntity() + "." + field.getName() + "' <- '" +
                    relationshipOther.getEntity() + "'"
            );
        }

        if (field.getJavaType() == JavaType.OBJECT) {
            if (Utils.not(
                relationshipOther.getType() == Relationship.Type.ONE_TO_ONE
                    && relationship.getType() == Relationship.Type.ONE_TO_ONE
            )) {
                throw new EntityValidationException(
                    "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + dependencyInfo.getField().getName() + "'"
                );
            }
        }

        if (field.getJavaType() == JavaType.ARRAY) {
            if (Utils.not(
                relationshipOther.getType() == Relationship.Type.MANY_TO_ONE
                    && relationship.getType() == Relationship.Type.ONE_TO_MANY
            )) {
                throw new EntityValidationException(
                    "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "'"
                );
            }
        }
    }

    private void checkAllForeignColumnExistsInOppositeEntityDbMapping() {
        String oppositeEntity = field.getRelationship().get().getEntity();

        mapping.getForeignColumnMappingList().forEach(foreignColumnMapping -> {
            boolean existInCombinedColumns = internalEntityValidator.columnExistsInCombinedColumns(oppositeEntity, foreignColumnMapping.getSrcColumn());

            if (Utils.not(existInCombinedColumns)) {
                throw new EntityValidationException("");
            }
        });
    }

    private void checkFieldType() {

        boolean isFieldTypeOk = field.getJavaType() == JavaType.OBJECT || field.getJavaType() == JavaType.ARRAY;
        if (Utils.not(isFieldTypeOk)) {
            throw new EntityValidationException("Field '" + field.getName() + "' has an invalid type '" + field.getJavaType() + "' for dbColumnMappingType '"
                + mapping.getColumnType() + "'");
        }
    }

}
