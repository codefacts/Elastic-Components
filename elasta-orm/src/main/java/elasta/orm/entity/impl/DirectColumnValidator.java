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
final class DirectColumnValidator {
    final InternalEntityValidator internalEntityValidator;
    final Entity entity;
    final Field field;
    final DirectRelationMapping mapping;

    public DirectColumnValidator(InternalEntityValidator internalEntityValidator, Entity entity, Field field, DirectRelationMapping mapping) {
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

        dependencyTplOptional.flatMap(dependencyTpl -> dependencyTpl.getFieldToDependencyInfoMap().values().stream()
            .filter(this::findDependencyInfoInOppositeSide)
            .findAny()).ifPresent(dependencyInfo -> {

            DependencyTpl dependencyTpl = dependencyTplOptional.get();

            Relationship relationshipOther = dependencyInfo.getField().getRelationship().orElseThrow(() -> new EntityValidationException("No relationship present for mapping '" + mapping + "' in table '" + dependencyTpl.getEntity().getDbMapping().getTable() + "'"));

            checkRelationshipTypeAndJavaType(dependencyInfo, dependencyTpl, relationship, relationshipOther);
        });
    }

    private void checkRelationshipTypeAndJavaType(DependencyInfo dependencyInfo, DependencyTpl dependencyTpl, Relationship relationship, Relationship relationshipOther) {

        if (dependencyInfo.getField().getJavaType() == JavaType.OBJECT) {
            if (Utils.not(
                relationshipOther.getType() == Relationship.Type.ONE_TO_ONE
                    && relationship.getType() == Relationship.Type.ONE_TO_ONE
            )) {

                throw new EntityValidationException(
                    "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + dependencyInfo.getField().getName() + "'"
                );
            }
        }

        if (dependencyInfo.getField().getJavaType() == JavaType.ARRAY) {
            if (Utils.not(
                relationshipOther.getType() == Relationship.Type.ONE_TO_MANY
                    && relationship.getType() == Relationship.Type.MANY_TO_ONE
            )) {
                throw new EntityValidationException(
                    "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + dependencyInfo.getField().getName() + "'"
                );
            }
        }
    }

    private boolean findDependencyInfoInOppositeSide(DependencyInfo dependencyInfo) {

        if (dependencyInfo.getRelationMapping().getColumnType() == RelationType.VIRTUAL) {

            VirtualRelationMapping depMapping = (VirtualRelationMapping) dependencyInfo.getRelationMapping();

            return internalEntityValidator.isEqualDirectAndVirtual(
                mapping.getForeignColumnMappingList(), depMapping.getForeignColumnMappingList()
            );
        }
        return false;
    }

    private void checkRelationshipType(Relationship relationship) {

        if (Utils.not(relationship.getType() == Relationship.Type.ONE_TO_ONE || relationship.getType() == Relationship.Type.MANY_TO_ONE)) {
            throw new EntityValidationException(
                "Relationship type '" + relationship.getType() + "' is invalid for mapping type '" + mapping.getColumnType() + "'"
            );
        }
    }

    private void checkFieldType() {

        boolean isFieldTypeOk = field.getJavaType() == JavaType.OBJECT;

        if (Utils.not(isFieldTypeOk)) {
            throw new EntityValidationException("Field '" + field.getName() + "' has an invalid type '" + field.getJavaType() + "' for dbColumnMappingType '"
                + mapping.getColumnType() + "'");
        }
    }
}
