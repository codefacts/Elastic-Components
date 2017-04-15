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
        boolean isFieldTypeOk = field.getJavaType() == JavaType.OBJECT || field.getJavaType() == JavaType.ARRAY;
        if (Utils.not(isFieldTypeOk)) {
            throw new EntityValidationException("Field '" + field.getName() + "' has an invalid type '" + field.getJavaType() + "' for dbColumnMappingType '"
                + mapping.getColumnType() + "'");
        }

        Optional<DependencyTpl> dependencyTplOptional = internalEntityValidator.checkCommonRelationalValidity(mapping);

//                checkAllColumnExistInBothTables(
//                    dependencyTpl.getEntity().getDbMapping(),
//                    entity.getDbMapping(),
//                    mapping.getForeignColumnMappingList()
//                );

        DependencyInfo info = dependencyTplOptional.flatMap(dependencyTpl -> {

            return dependencyTpl.getFieldToDependencyInfoMap().values().stream()
                .filter(dependencyInfo -> {
                    if (dependencyInfo.getRelationMapping().getColumnType() == RelationType.DIRECT) {
                        DirectRelationMapping depMapping = (DirectRelationMapping) dependencyInfo.getRelationMapping();

                        if (mapping.getForeignColumnMappingList().size() != depMapping.getForeignColumnMappingList().size()) {
                            throw new EntityValidationException(
                                "mapping.getForeignColumnMappingList().size() != depMapping.getForeignColumnMappingList().size() in relationship '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "'"
                            );
                        }

                        if (internalEntityValidator.isEqualDirectAndVirtual(
                            depMapping.getForeignColumnMappingList(), mapping.getForeignColumnMappingList()
                        )) {
                            return true;
                        }
                    }
                    return false;
                })
                .findAny();
        }).orElseThrow(() -> new EntityValidationException("No Mapping found in the opposite side for mapping '" + mapping + "' in relationship '" + entity.getName() + "." + field.getName() + "' <- '" + dependencyTplOptional.map(dependencyTpl -> dependencyTpl.getEntity().getName()).orElse("") + "'"));

        DependencyTpl dependencyTpl = dependencyTplOptional.get();

        Relationship relationship = field.getRelationship().orElseThrow(() -> new EntityValidationException("No relationship present for mapping '" + mapping + "' in table '" + entity.getDbMapping().getTable() + "'"));

        internalEntityValidator.checkFieldTypeAndName(relationship, field);

        Relationship relationshipOther = info.getField().getRelationship().orElseThrow(() -> new EntityValidationException("No relationship present for mapping '" + mapping + "' in table '" + dependencyTpl.getEntity().getDbMapping().getTable() + "'"));

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
                    "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + info.getField().getName() + "'"
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

}
