package elasta.orm.entity.core.columnmapping.impl;

import elasta.orm.entity.core.RelationType;
import elasta.orm.entity.core.ForeignColumnMapping;
import elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions;
import elasta.orm.entity.core.columnmapping.IndirectRelationMapping;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
@Value
final public class IndirectRelationMappingImpl implements IndirectRelationMapping {
    final String referencingTable;
    final String referencingEntity;
    final String relationTable;
    final List<ForeignColumnMapping> srcForeignColumnMappingList;
    final List<ForeignColumnMapping> dstForeignColumnMappingList;
    final String field;
    final RelationType columnType;
    final DirectRelationMappingOptions options;

    public IndirectRelationMappingImpl(String referencingTable, String referencingEntity, String relationTable, List<ForeignColumnMapping> srcForeignColumnMappingList, List<ForeignColumnMapping> dstForeignColumnMappingList, String field, DirectRelationMappingOptions options) {
        Objects.requireNonNull(referencingTable);
        Objects.requireNonNull(referencingEntity);
        Objects.requireNonNull(relationTable);
        Objects.requireNonNull(srcForeignColumnMappingList);
        Objects.requireNonNull(dstForeignColumnMappingList);
        Objects.requireNonNull(field);
        Objects.requireNonNull(options);
        this.referencingTable = referencingTable;
        this.referencingEntity = referencingEntity;
        this.relationTable = relationTable;
        this.srcForeignColumnMappingList = srcForeignColumnMappingList;
        this.dstForeignColumnMappingList = dstForeignColumnMappingList;
        this.field = field;
        this.columnType = RelationType.INDIRECT;
        this.options = options;
    }

    @Override
    public String getReferencingTable() {
        return referencingTable;
    }

    @Override
    public String getReferencingEntity() {
        return referencingEntity;
    }

    @Override
    public DirectRelationMappingOptions getOptions() {
        return options;
    }

    public String getRelationTable() {
        return relationTable;
    }

    @Override
    public List<ForeignColumnMapping> getSrcForeignColumnMappingList() {
        return srcForeignColumnMappingList;
    }

    @Override
    public List<ForeignColumnMapping> getDstForeignColumnMappingList() {
        return dstForeignColumnMappingList;
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public RelationType getColumnType() {
        return columnType;
    }
}
