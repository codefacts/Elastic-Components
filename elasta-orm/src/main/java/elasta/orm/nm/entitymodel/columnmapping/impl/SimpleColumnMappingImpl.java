package elasta.orm.nm.entitymodel.columnmapping.impl;

import elasta.orm.nm.entitymodel.ColumnType;
import elasta.orm.nm.entitymodel.DbType;
import elasta.orm.nm.entitymodel.columnmapping.SimpleColumnMapping;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
public class SimpleColumnMappingImpl implements SimpleColumnMapping {
    final String field;
    final String column;
    final ColumnType columnType;
    final DbType dbType;

    public SimpleColumnMappingImpl(String field, String column, DbType dbType) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(column);
        Objects.requireNonNull(dbType);
        this.field = field;
        this.column = column;
        this.columnType = ColumnType.SIMPLE;
        this.dbType = dbType;
    }

    @Override
    public String getField() {
        return field;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public ColumnType getColumnType() {
        return columnType;
    }

    @Override
    public DbType getDbType() {
        return dbType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleColumnMappingImpl that = (SimpleColumnMappingImpl) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (columnType != that.columnType) return false;
        return dbType == that.dbType;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (column != null ? column.hashCode() : 0);
        result = 31 * result + (columnType != null ? columnType.hashCode() : 0);
        result = 31 * result + (dbType != null ? dbType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleColumnMappingImpl{" +
            "field='" + field + '\'' +
            ", name='" + column + '\'' +
            ", columnType=" + columnType +
            ", dbType=" + dbType +
            '}';
    }
}
