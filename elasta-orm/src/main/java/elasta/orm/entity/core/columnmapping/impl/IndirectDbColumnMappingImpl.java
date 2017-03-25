package elasta.orm.entity.core.columnmapping.impl;

import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.ForeignColumnMapping;
import elasta.orm.entity.core.columnmapping.IndirectDbColumnMapping;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
@Value
final public class IndirectDbColumnMappingImpl implements IndirectDbColumnMapping {
    final String referencingTable;
    final String referencingEntity;
    final String relationTable;
    final List<ForeignColumnMapping> srcForeignColumnMappingList;
    final List<ForeignColumnMapping> dstForeignColumnMappingList;
    final String field;
    final ColumnType columnType;

    public IndirectDbColumnMappingImpl(String referencingTable, String referencingEntity, String relationTable, List<ForeignColumnMapping> srcForeignColumnMappingList, List<ForeignColumnMapping> dstForeignColumnMappingList, String field) {
        Objects.requireNonNull(referencingTable);
        Objects.requireNonNull(referencingEntity);
        Objects.requireNonNull(relationTable);
        Objects.requireNonNull(srcForeignColumnMappingList);
        Objects.requireNonNull(dstForeignColumnMappingList);
        Objects.requireNonNull(field);
        this.referencingTable = referencingTable;
        this.referencingEntity = referencingEntity;
        this.relationTable = relationTable;
        this.srcForeignColumnMappingList = srcForeignColumnMappingList;
        this.dstForeignColumnMappingList = dstForeignColumnMappingList;
        this.field = field;
        this.columnType = ColumnType.INDIRECT;
    }

    @Override
    public String getReferencingTable() {
        return referencingTable;
    }

    @Override
    public String getReferencingEntity() {
        return referencingEntity;
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
    public ColumnType getColumnType() {
        return columnType;
    }
}
