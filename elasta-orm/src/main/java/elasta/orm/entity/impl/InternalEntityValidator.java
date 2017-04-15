package elasta.orm.entity.impl;

import elasta.commons.Utils;
import elasta.orm.entity.DependencyTpl;
import elasta.orm.entity.EntityUtils;
import elasta.orm.entity.EntityValidator;
import elasta.orm.entity.TableDependency;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.entity.ex.EntityValidationException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 4/14/2017.
 */
final class InternalEntityValidator {
    final Entity entity;
    final Map<String, Field> fieldNameToFieldMap;
    final Map<String, ColumnMapping> fieldToColumnMappingMap;
    final Map<String, RelationMapping> fieldToRelationMappingMap;
    private final Map<String, TableDependency> tableToTableDependencyMap;
    private final Map<String, Entity> entityNameToEntityMap;

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

    void checkAllColumnExistInBothTables(DbMapping srcDbMapping, DbMapping dstDbMapping, List<ForeignColumnMapping> foreignColumnMappingList) {
        Map<String, ColumnMapping> srcColumnMappingMap = EntityUtils.toColumnNameToColumnMapingMap(srcDbMapping.getColumnMappings());
        Map<String, ColumnMapping> dstColumnMappingMap = EntityUtils.toColumnNameToColumnMapingMap(dstDbMapping.getColumnMappings());

        foreignColumnMappingList.forEach(foreignColumnMapping -> {
            if (Utils.not(
                srcColumnMappingMap.containsKey(foreignColumnMapping.getSrcColumn())
            )) {
                throw new EntityValidationException(
                    "srcColumn '" + foreignColumnMapping.getSrcColumn() + "' is not present in the column list in table '" + srcDbMapping.getTable() + "'"
                );
            }

            if (Utils.not(
                dstColumnMappingMap.containsKey(foreignColumnMapping.getDstColumn())
            )) {
                throw new EntityValidationException(
                    "srcColumn '" + foreignColumnMapping.getSrcColumn() + "' is not present in the column list in table '" + dstDbMapping.getTable() + "'"
                );
            }
        });
    }

    Optional<DependencyTpl> checkCommonRelationalValidity(RelationMapping mapping) {
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

    boolean isEqualDirectAndVirtual(List<ForeignColumnMapping> foreignColumnMappingList, List<ForeignColumnMapping> foreignColumnMappingList1) {
        for (int i = 0; i < foreignColumnMappingList.size(); i++) {
            ForeignColumnMapping mapping1 = foreignColumnMappingList.get(i);
            ForeignColumnMapping mapping2 = foreignColumnMappingList1.get(i);
            if (
                Utils.not(
                    mapping1.getSrcColumn().equals(
                        mapping2.getSrcColumn()
                    )
                        &&
                        mapping1.getDstColumn().equals(
                            mapping2.getDstColumn()
                        )
                )
                ) {
                return false;
            }
        }
        return true;
    }

    boolean isEqualBothSide(List<ForeignColumnMapping> srcForeignColumnMappingList, List<ForeignColumnMapping> dstForeignColumnMappingList) {
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
            dstMapping.getDstColumn()
        ) && srcMapping.getDstColumn().equals(
            dstMapping.getSrcColumn()
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
