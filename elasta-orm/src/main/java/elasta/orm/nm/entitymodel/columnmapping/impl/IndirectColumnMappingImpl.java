package elasta.orm.nm.entitymodel.columnmapping.impl;

import elasta.orm.nm.entitymodel.ColumnType;
import elasta.orm.nm.entitymodel.ForeignColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.IndirectColumnMapping;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
public class IndirectColumnMappingImpl implements IndirectColumnMapping {
    final String referencingTable;
    final String referencingEntity;
    final String relationTable;
    final List<ForeignColumnMapping> srcForeignColumnMappingList;
    final List<ForeignColumnMapping> dstForeignColumnMappingList;
    final String field;
    final ColumnType columnType;

    public IndirectColumnMappingImpl(String referencingTable, String referencingEntity, String relationTable, List<ForeignColumnMapping> srcForeignColumnMappingList, List<ForeignColumnMapping> dstForeignColumnMappingList, String field) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndirectColumnMappingImpl that = (IndirectColumnMappingImpl) o;

        if (referencingTable != null ? !referencingTable.equals(that.referencingTable) : that.referencingTable != null)
            return false;
        if (relationTable != null ? !relationTable.equals(that.relationTable) : that.relationTable != null)
            return false;
        if (srcForeignColumnMappingList != null ? !srcForeignColumnMappingList.equals(that.srcForeignColumnMappingList) : that.srcForeignColumnMappingList != null)
            return false;
        if (dstForeignColumnMappingList != null ? !dstForeignColumnMappingList.equals(that.dstForeignColumnMappingList) : that.dstForeignColumnMappingList != null)
            return false;
        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return columnType == that.columnType;

    }

    @Override
    public int hashCode() {
        int result = referencingTable != null ? referencingTable.hashCode() : 0;
        result = 31 * result + (relationTable != null ? relationTable.hashCode() : 0);
        result = 31 * result + (srcForeignColumnMappingList != null ? srcForeignColumnMappingList.hashCode() : 0);
        result = 31 * result + (dstForeignColumnMappingList != null ? dstForeignColumnMappingList.hashCode() : 0);
        result = 31 * result + (field != null ? field.hashCode() : 0);
        result = 31 * result + (columnType != null ? columnType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IndirectColumnMappingImpl{" +
            "referencingTable='" + referencingTable + '\'' +
            ", relationTable='" + relationTable + '\'' +
            ", srcForeignColumnMappingList=" + srcForeignColumnMappingList +
            ", dstForeignColumnMappingList=" + dstForeignColumnMappingList +
            ", field='" + field + '\'' +
            ", columnType=" + columnType +
            '}';
    }
}
