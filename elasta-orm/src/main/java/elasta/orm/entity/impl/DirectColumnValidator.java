package elasta.orm.entity.impl;

import elasta.commons.Utils;
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
        boolean isFieldTypeOk = field.getJavaType() == JavaType.OBJECT;
        if (Utils.not(isFieldTypeOk)) {
            throw new EntityValidationException("Field '" + field.getName() + "' has an invalid type '" + field.getJavaType() + "' for dbColumnMappingType '"
                + mapping.getColumnType() + "'");
        }

        Relationship relationship = field.getRelationship()
            .orElseThrow(() -> new EntityValidationException("No relationship present for mapping '" + mapping + "' in table '" + entity.getDbMapping().getTable() + "'"));

        internalEntityValidator.checkFieldTypeAndName(relationship, field);

        if (Utils.not(relationship.getType() == Relationship.Type.ONE_TO_ONE || relationship.getType() == Relationship.Type.MANY_TO_ONE)) {
            throw new EntityValidationException(
                "Relationship type '" + relationship.getType() + "' is invalid for mapping type '" + mapping.getColumnType() + "'"
            );
        }

        Optional<DependencyTpl> dependencyTplOptional = internalEntityValidator.checkCommonRelationalValidity(mapping);

//                checkAllColumnExistInBothTables(
//                    entity.getDbMapping(),
//                    dependencyTpl.getEntity().getDbMapping(),
//                    mapping.getForeignColumnMappingList()
//                );

        dependencyTplOptional.flatMap(dependencyTpl -> {

            return dependencyTpl.getFieldToDependencyInfoMap().values().stream()
                .filter(dependencyInfo -> {
                    if (dependencyInfo.getRelationMapping().getColumnType() == RelationType.VIRTUAL) {
                        VirtualRelationMapping depMapping = (VirtualRelationMapping) dependencyInfo.getRelationMapping();
                        if (mapping.getForeignColumnMappingList().size() != depMapping.getForeignColumnMappingList().size()) {
                            throw new EntityValidationException(
                                "mapping.getForeignColumnMappingList().size() != depMapping.getForeignColumnMappingList().size() in relationship '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "'"
                            );
                        }
                        if (internalEntityValidator.isEqualDirectAndVirtual(
                            mapping.getForeignColumnMappingList(), depMapping.getForeignColumnMappingList()
                        )) {
                            return true;
                        }
                    }
                    return false;
                })
                .findAny();

        }).ifPresent(info -> {

            DependencyTpl dependencyTpl = dependencyTplOptional.get();

            Relationship relationshipOther = info.getField().getRelationship().orElseThrow(() -> new EntityValidationException("No relationship present for mapping '" + mapping + "' in table '" + dependencyTpl.getEntity().getDbMapping().getTable() + "'"));

            if (info.getField().getJavaType() == JavaType.OBJECT) {
                if (Utils.not(
                    relationshipOther.getType() == Relationship.Type.ONE_TO_ONE
                        && relationship.getType() == Relationship.Type.ONE_TO_ONE
                )) {

                    throw new EntityValidationException(
                        "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + info.getField().getName() + "'"
                    );
                }
            }

            if (info.getField().getJavaType() == JavaType.ARRAY) {
                if (Utils.not(
                    relationshipOther.getType() == Relationship.Type.ONE_TO_MANY
                        && relationship.getType() == Relationship.Type.MANY_TO_ONE
                )) {
                    throw new EntityValidationException(
                        "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + info.getField().getName() + "'"
                    );
                }
            }
        });
    }
}
