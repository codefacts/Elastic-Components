package elasta.orm.entity.core.columnmapping.impl;

import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.columnmapping.VirtualDbColumnMapping;
import elasta.orm.entity.core.ForeignColumnMapping;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
final public class VirtualDbColumnMappingImpl implements VirtualDbColumnMapping {
    final String referencingTable;
    final String referencingEntity;
    final List<ForeignColumnMapping> foreignColumnMappingList;
    final String field;
    final ColumnType columnType;

    public VirtualDbColumnMappingImpl(String referencingTable, String referencingEntity, List<ForeignColumnMapping> foreignColumnMappingList, String field) {
        Objects.requireNonNull(referencingTable);
        Objects.requireNonNull(referencingEntity);
        Objects.requireNonNull(foreignColumnMappingList);
        Objects.requireNonNull(field);
        this.referencingTable = referencingTable;
        this.referencingEntity = referencingEntity;
        this.foreignColumnMappingList = foreignColumnMappingList;
        this.field = field;
        this.columnType = ColumnType.VIRTUAL;
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
    public List<ForeignColumnMapping> getForeignColumnMappingList() {
        return foreignColumnMappingList;
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

        VirtualDbColumnMappingImpl that = (VirtualDbColumnMappingImpl) o;

        if (referencingTable != null ? !referencingTable.equals(that.referencingTable) : that.referencingTable != null)
            return false;
        if (foreignColumnMappingList != null ? !foreignColumnMappingList.equals(that.foreignColumnMappingList) : that.foreignColumnMappingList != null)
            return false;
        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return columnType == that.columnType;

    }

    @Override
    public int hashCode() {
        int result = referencingTable != null ? referencingTable.hashCode() : 0;
        result = 31 * result + (foreignColumnMappingList != null ? foreignColumnMappingList.hashCode() : 0);
        result = 31 * result + (field != null ? field.hashCode() : 0);
        result = 31 * result + (columnType != null ? columnType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VirtualDbColumnMappingImpl{" +
            "referencingTable='" + referencingTable + '\'' +
            ", foreignColumnMappingList=" + foreignColumnMappingList +
            ", field='" + field + '\'' +
            ", columnType=" + columnType +
            '}';
    }
}
