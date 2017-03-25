package elasta.orm.entity.core.columnmapping.impl;

import elasta.orm.entity.core.ColumnType;
import elasta.orm.entity.core.columnmapping.VirtualDbColumnMapping;
import elasta.orm.entity.core.ForeignColumnMapping;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
@Value
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
}
