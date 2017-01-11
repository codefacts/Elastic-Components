package elasta.orm.nm.entitymodel.columnmapping.impl;

import elasta.orm.nm.entitymodel.ColumnType;
import elasta.orm.nm.entitymodel.ForeignColumnMapping;
import elasta.orm.nm.entitymodel.columnmapping.DirectColumnMapping;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
final public class DirectColumnMappingImpl implements DirectColumnMapping {
    final String referencingTable;
    final List<ForeignColumnMapping> foreignColumnMappingList;
    final String field;
    final ColumnType columnType;

    public DirectColumnMappingImpl(String referencingTable, List<ForeignColumnMapping> foreignColumnMappingList, String field, ColumnType columnType) {
        Objects.requireNonNull(referencingTable);
        Objects.requireNonNull(foreignColumnMappingList);
        Objects.requireNonNull(field);
        Objects.requireNonNull(columnType);
        this.referencingTable = referencingTable;
        this.foreignColumnMappingList = foreignColumnMappingList;
        this.field = field;
        this.columnType = columnType;
    }

    @Override
    public String getReferencingTable() {
        return referencingTable;
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

        DirectColumnMappingImpl that = (DirectColumnMappingImpl) o;

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
        return "DirectColumnMappingImpl{" +
            "referencingTable='" + referencingTable + '\'' +
            ", foreignColumnMappingList=" + foreignColumnMappingList +
            ", field='" + field + '\'' +
            ", columnType=" + columnType +
            '}';
    }
}