package elasta.orm.entity.core.columnmapping.impl;

import elasta.orm.entity.core.ColumnType;import elasta.orm.entity.core.DbType;
import elasta.orm.entity.core.columnmapping.SimpleDbColumnMapping;
import lombok.Value;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
@Value
final public class SimpleDbColumnMappingImpl implements SimpleDbColumnMapping {
    final String field;
    final String column;
    final ColumnType columnType;
    final DbType dbType;

    public SimpleDbColumnMappingImpl(String field, String column, DbType dbType) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(column);
        Objects.requireNonNull(dbType);
        this.field = field;
        this.column = column;
        this.dbType = dbType;
        this.columnType = ColumnType.SIMPLE;
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
}
