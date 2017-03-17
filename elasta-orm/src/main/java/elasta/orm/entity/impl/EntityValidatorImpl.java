package elasta.orm.entity.impl;

import elasta.commons.Utils;
import elasta.orm.entity.*;
import elasta.orm.entity.core.*;
import elasta.orm.entity.core.columnmapping.*;
import elasta.orm.entity.ex.EntityValidationException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/17/2017.
 */
final public class EntityValidatorImpl implements EntityValidator {
    @Override
    public void validate(Params params) {
        new InternalEntityValidator(params).validate();
    }

    private class InternalEntityValidator {
        final Entity entity;
        final Map<String, Field> fieldNameToFieldMap;
        final Map<String, DbColumnMapping> fieldToDbColumnMappingMap;
        private final Map<String, TableDependency> tableToTableDependencyMap;
        private final Map<String, Entity> entityNameToEntityMap;

        public InternalEntityValidator(Params params) {
            Objects.requireNonNull(params);
            this.entity = params.getEntity();
            this.tableToTableDependencyMap = params.getTableToTabledependencyMap();
            this.entityNameToEntityMap = params.getEntityNameToEntityMap();
            this.fieldNameToFieldMap = EntityUtils.toFieldNameToFieldMap(params.getEntity().getFields());
            fieldToDbColumnMappingMap = EntityUtils.toFieldToDbColumnMappingMap(
                entity.getDbMapping().getDbColumnMappings()
            );
        }

        public void validate() {
            valdatePrimaryKey();

            for (Field field : entity.getFields()) {
                validateField(field);
            }
        }

        private void validateField(Field field) {
            boolean containsKey = fieldToDbColumnMappingMap.containsKey(field.getName());
            if (Utils.not(containsKey)) {
                throw new EntityValidationException("No DbColumnMapping found for field '" + field.getName() + "'");
            }

            DbColumnMapping dbColumnMapping = fieldToDbColumnMappingMap.get(field.getName());

            switch (dbColumnMapping.getColumnType()) {
                case SIMPLE: {
                    SimpleDbColumnMapping mapping = (SimpleDbColumnMapping) dbColumnMapping;
                    final boolean typeIsOk = field.getJavaType() != JavaType.OBJECT && field.getJavaType() != JavaType.ARRAY;
                    if (Utils.not(typeIsOk)) {
                        throw new EntityValidationException("Type '" + field.getJavaType() + "' of Field '" + field.getName() + "' is not supported for mapping '" + SimpleDbColumnMapping.class.getSimpleName() + "'");
                    }
                    if (field.getRelationship().isPresent()) {
                        throw new EntityValidationException("Field '" + field.getName() + "' can not have relationship '" + field.getRelationship() + "' with dbColumnType '" + dbColumnMapping.getColumnType() + "'");
                    }
                }
                break;
                case INDIRECT: {

                    new IndirectColumnMappingValidator(entity, field, (IndirectDbColumnMapping) dbColumnMapping).validate();
                }
                break;
                case VIRTUAL: {
                    new IndirectColumnValidator(
                        entity,
                        field,
                        (VirtualDbColumnMapping) dbColumnMapping
                    ).validate();
                }
                break;
                case DIRECT: {
                    new DirectColumnValidator(
                        entity,
                        field,
                        (DirectDbColumnMapping) dbColumnMapping
                    ).validate();
                }
                break;
            }
        }

        private void checkAllColumnExistInBothTables(DbColumnMapping[] srcDbColumnMappings, DbColumnMapping[] dstDbColumnMappings, List<ForeignColumnMapping> foreignColumnMappingList) {
            Map<String, SimpleDbColumnMapping> srcColumnMappingMap = EntityUtils.toSimpleDbColumnNameToSimpleDbColumnMapingMap(srcDbColumnMappings);
            Map<String, SimpleDbColumnMapping> dstColumnMappingMap = EntityUtils.toSimpleDbColumnNameToSimpleDbColumnMapingMap(dstDbColumnMappings);

            foreignColumnMappingList.forEach(foreignColumnMapping -> {
                if (Utils.not(
                    srcColumnMappingMap.containsKey(foreignColumnMapping.getSrcColumn())
                )) {
                    throw new EntityValidationException(
                        "srcColumn '" + foreignColumnMapping.getSrcColumn() + "' is not present in the column list"
                    );
                }

                if (Utils.not(
                    dstColumnMappingMap.containsKey(foreignColumnMapping.getDstColumn())
                )) {
                    throw new EntityValidationException(
                        "dstColumn '" + foreignColumnMapping.getDstColumn() + "' is not present in the column list"
                    );
                }
            });
        }

        private DependencyTpl checkCommonRelationalValidity(RelationMapping mapping) {
            DependencyTpl dependencyTpl = getDependencyTpl(entity, mapping);

            boolean mappingEntityInfoIsCorrect = mapping.getReferencingTable().equals(dependencyTpl.getEntity().getDbMapping().getTable())
                && mapping.getReferencingEntity().equals(dependencyTpl.getEntity().getName());

            if (Utils.not(mappingEntityInfoIsCorrect)) {
                throw new EntityValidationException("Referencing Entity '" + mapping.getReferencingEntity() + "' or Table '" + mapping.getReferencingTable() + "' does not match with actual Referencing Entity '" + dependencyTpl.getEntity().getName() + "' or Table '" + dependencyTpl.getEntity().getDbMapping().getTable() + "'");
            }
            return dependencyTpl;
        }

        private boolean isEqualDirectAndVirtual(List<ForeignColumnMapping> foreignColumnMappingList, List<ForeignColumnMapping> foreignColumnMappingList1) {
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

        private boolean isEqualBothSide(List<ForeignColumnMapping> srcForeignColumnMappingList, List<ForeignColumnMapping> dstForeignColumnMappingList) {
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

        private DependencyTpl getDependencyTpl(Entity entity, RelationMapping mapping) {
            TableDependency tableDependency = tableToTableDependencyMap.get(entity.getDbMapping().getTable());
            if (tableDependency == null) {
                throw new EntityValidationException("No TableDependency found for table '" + entity.getDbMapping().getTable() + " that has a mapping '" + mapping + "'");
            }
            Map<String, DependencyTpl> tableToDependencyInfoMap = tableDependency.getTableToDependencyInfoMap();

            return tableToDependencyInfoMap.get(mapping.getReferencingTable());
        }

        private void valdatePrimaryKey() {
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

            boolean containsDbColumn = fieldToDbColumnMappingMap.containsKey(field.getName());
            if (Utils.not(containsDbColumn)) {
                throw new EntityValidationException("No DbColumnMapping found for primary key '" + entity.getPrimaryKey() + "'");
            }
        }


        final private class IndirectColumnMappingValidator {
            final Entity entity;
            final Field field;
            final IndirectDbColumnMapping mapping;

            public IndirectColumnMappingValidator(Entity entity, Field field, IndirectDbColumnMapping mapping) {
                Objects.requireNonNull(entity);
                Objects.requireNonNull(field);
                Objects.requireNonNull(mapping);
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

                if (Utils.not(
                    tableToTableDependencyMap.containsKey(entity.getDbMapping().getTable())
                )) {

                    final Entity referencingEntity = entityNameToEntityMap.get(mapping.getReferencingEntity());
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

                    checkAllSrcColumnsExistsInDbColumnMappings(entity.getDbMapping().getDbColumnMappings(), mapping.getSrcForeignColumnMappingList());
                    checkAllSrcColumnsExistsInDbColumnMappings(referencingEntity.getDbMapping().getDbColumnMappings(), mapping.getDstForeignColumnMappingList());

                    return;
                }

                DependencyTpl dependencyTpl = checkCommonRelationalValidity(mapping);

                Optional<DependencyInfo> info = dependencyTpl.getFieldToDependencyInfoMap().values().stream()
                    .filter(dependencyInfo -> {
                        if (dependencyInfo.getDbColumnMapping().getColumnType() == ColumnType.INDIRECT) {

                            IndirectDbColumnMapping depMapping = (IndirectDbColumnMapping) dependencyInfo.getDbColumnMapping();

                            if (mapping.getRelationTable().equals(depMapping.getRelationTable())) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .findAny();

                if (info.isPresent()) {

                    DependencyInfo dependencyInfo = info.get();

                    checkMappingValidity(
                        mapping,
                        (IndirectDbColumnMapping) dependencyInfo.getDbColumnMapping(),
                        dependencyTpl,
                        dependencyInfo
                    );

                } else {
                    checkAllSrcColumnsExistsInDbColumnMappings(
                        entity.getDbMapping().getDbColumnMappings(), mapping.getSrcForeignColumnMappingList()
                    );
                    checkAllSrcColumnsExistsInDbColumnMappings(
                        entityNameToEntityMap.get(mapping.getReferencingEntity()).getDbMapping().getDbColumnMappings(), mapping.getDstForeignColumnMappingList()
                    );
                }

                Relationship relationship = field.getRelationship().get();
                checkFieldTypeAndName(relationship, field);

                Relationship relationshipOther = info.get().getField().getRelationship().get();

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
                        "Relationship type '" + field.getRelationship().get().getType() + "' is invalid for mapping type '" + mapping.getColumnType() + "'"
                    );
                }
            }

            private void checkMappingValidity(IndirectDbColumnMapping mapping, IndirectDbColumnMapping depMapping, DependencyTpl dependencyTpl, DependencyInfo dependencyInfo) {
                List<ForeignColumnMapping> srcForeignColumnMappingList = mapping.getSrcForeignColumnMappingList();
                List<ForeignColumnMapping> dstForeignColumnMappingList = mapping.getDstForeignColumnMappingList();
                List<ForeignColumnMapping> srcForeignColumnMappingList1 = depMapping.getSrcForeignColumnMappingList();
                List<ForeignColumnMapping> dstForeignColumnMappingList1 = depMapping.getDstForeignColumnMappingList();

                if (srcForeignColumnMappingList.size() > 0
                    && dstForeignColumnMappingList1.size() > 0
                    && Utils.not(
                    isEqualBothSide(srcForeignColumnMappingList, dstForeignColumnMappingList1)
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
                    isEqualBothSide(srcForeignColumnMappingList1, dstForeignColumnMappingList)
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

                checkAllSrcColumnsExistsInDbColumnMappings(
                    entity.getDbMapping().getDbColumnMappings(),
                    srcForeignColumnMappingList.size() > 0 ? srcForeignColumnMappingList : dstForeignColumnMappingList1
                );

                checkAllSrcColumnsExistsInDbColumnMappings(
                    dependencyTpl.getEntity().getDbMapping().getDbColumnMappings(),
                    dstForeignColumnMappingList.size() > 0 ? dstForeignColumnMappingList : srcForeignColumnMappingList1
                );
            }

            private void checkAllSrcColumnsExistsInDbColumnMappings(DbColumnMapping[] dbColumnMappings, List<ForeignColumnMapping> foreignColumnMappings) {
                Map<String, SimpleDbColumnMapping> map = EntityUtils.toSimpleDbColumnNameToSimpleDbColumnMapingMap(dbColumnMappings);
                foreignColumnMappings.forEach(foreignColumnMapping -> {
                    if (
                        Utils.not(map.containsKey(foreignColumnMapping.getSrcColumn()))
                        ) {
                        throw new EntityValidationException(
                            "Mapping column '" + foreignColumnMapping.getSrcColumn() + "' does not exists in table column list in table '" + entity.getDbMapping().getTable() + "'"
                        );
                    }
                });
            }
        }

        final private class DirectColumnValidator {
            final Entity entity;
            final Field field;
            final DirectDbColumnMapping mapping;

            public DirectColumnValidator(Entity entity, Field field, DirectDbColumnMapping mapping) {
                Objects.requireNonNull(entity);
                Objects.requireNonNull(field);
                Objects.requireNonNull(mapping);
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
                DependencyTpl dependencyTpl = checkCommonRelationalValidity(mapping);

                checkAllColumnExistInBothTables(
                    entity.getDbMapping().getDbColumnMappings(),
                    dependencyTpl.getEntity().getDbMapping().getDbColumnMappings(),
                    mapping.getForeignColumnMappingList()
                );

                DependencyInfo info = dependencyTpl.getFieldToDependencyInfoMap().values().stream()
                    .filter(dependencyInfo -> {
                        if (dependencyInfo.getDbColumnMapping().getColumnType() == ColumnType.VIRTUAL) {
                            VirtualDbColumnMapping depMapping = (VirtualDbColumnMapping) dependencyInfo.getDbColumnMapping();
                            if (isEqualDirectAndVirtual(
                                mapping.getForeignColumnMappingList(), depMapping.getForeignColumnMappingList()
                            )) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .findAny().orElseThrow(() -> new EntityValidationException("No Mapping found in the opposite side for mapping '" + mapping + "' in relationship '" + entity.getName() + "." + field.getName() + "' <- '" + dependencyTpl.getEntity().getName() + "'"));

                Relationship relationship = field.getRelationship().get();

                checkFieldTypeAndName(relationship, field);

                if (Utils.not(relationship.getType() == Relationship.Type.ONE_TO_ONE || relationship.getType() == Relationship.Type.MANY_TO_ONE)) {
                    throw new EntityValidationException(
                        "Relationship type '" + field.getRelationship().get().getType() + "' is invalid for mapping type '" + mapping.getColumnType() + "'"
                    );
                }

                if (info.getField().getJavaType() == JavaType.OBJECT) {
                    if (Utils.not(
                        info.getField().getRelationship().get().getType() == Relationship.Type.ONE_TO_ONE
                            && relationship.getType() == Relationship.Type.ONE_TO_ONE
                    )) {
                        throw new EntityValidationException(
                            "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + info.getField().getName() + "'"
                        );
                    }
                }

                if (info.getField().getJavaType() == JavaType.ARRAY) {
                    if (Utils.not(
                        info.getField().getRelationship().get().getType() == Relationship.Type.ONE_TO_MANY
                            && relationship.getType() == Relationship.Type.MANY_TO_ONE
                    )) {
                        throw new EntityValidationException(
                            "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + info.getField().getName() + "'"
                        );
                    }
                }
            }
        }

        final private class IndirectColumnValidator {
            final Entity entity;
            final Field field;
            final VirtualDbColumnMapping mapping;

            public IndirectColumnValidator(Entity entity, Field field, VirtualDbColumnMapping mapping) {
                Objects.requireNonNull(entity);
                Objects.requireNonNull(field);
                Objects.requireNonNull(mapping);
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

                DependencyTpl dependencyTpl = checkCommonRelationalValidity(mapping);

                checkAllColumnExistInBothTables(
                    dependencyTpl.getEntity().getDbMapping().getDbColumnMappings(),
                    entity.getDbMapping().getDbColumnMappings(),
                    mapping.getForeignColumnMappingList()
                );

                DependencyInfo info = dependencyTpl.getFieldToDependencyInfoMap().values().stream()
                    .filter(dependencyInfo -> {
                        if (dependencyInfo.getDbColumnMapping().getColumnType() == ColumnType.DIRECT) {
                            DirectDbColumnMapping depMapping = (DirectDbColumnMapping) dependencyInfo.getDbColumnMapping();
                            if (isEqualDirectAndVirtual(
                                depMapping.getForeignColumnMappingList(), mapping.getForeignColumnMappingList()
                            )) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .findAny().orElseThrow(() -> new EntityValidationException("No Mapping found in the opposite side for mapping '" + mapping + "' in relationship '" + entity.getName() + "." + field.getName() + "' <- '" + dependencyTpl.getEntity().getName() + "'"));

                Relationship relationship = field.getRelationship().get();

                checkFieldTypeAndName(relationship, field);

                if (Utils.not(relationship.getType() == Relationship.Type.ONE_TO_ONE || relationship.getType() == Relationship.Type.MANY_TO_ONE)) {
                    throw new EntityValidationException(
                        "Relationship type '" + field.getRelationship().get().getType() + "' is invalid for mapping type '" + mapping.getColumnType() + "'"
                    );
                }

                if (field.getJavaType() == JavaType.OBJECT) {
                    if (Utils.not(
                        info.getField().getRelationship().get().getType() == Relationship.Type.ONE_TO_ONE
                            && relationship.getType() == Relationship.Type.ONE_TO_ONE
                    )) {
                        throw new EntityValidationException(
                            "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + info.getField().getName() + "'"
                        );
                    }
                }

                if (field.getJavaType() == JavaType.ARRAY) {
                    if (Utils.not(
                        info.getField().getRelationship().get().getType() == Relationship.Type.MANY_TO_ONE
                            && relationship.getType() == Relationship.Type.ONE_TO_MANY
                    )) {
                        throw new EntityValidationException(
                            "invalid relationship type '" + relationship.getType() + "' in relation '" + entity.getName() + "." + field.getName() + "' -> '" + dependencyTpl.getEntity().getName() + "." + info.getField().getName() + "'"
                        );
                    }
                }
            }

        }

        private void checkFieldTypeAndName(Relationship relationship, Field field) {
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

    public static void main(String[] afasdf) {
        looP:
        {
            if (true) {
                break looP;
            }
            System.out.println("KKKK");
        }
        System.out.println("ok");
    }
}
