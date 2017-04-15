package elasta.orm.entity.core.columnmapping.impl;

import elasta.orm.entity.core.DbType;
import elasta.orm.entity.core.columnmapping.ColumnMapping;
import lombok.Value;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
@Value
final public class ColumnMappingImpl implements ColumnMapping {
    final String field;
    final String column;
    final DbType dbType;

    public ColumnMappingImpl(String field, String column, DbType dbType) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(column);
        Objects.requireNonNull(dbType);
        this.field = field;
        this.column = column;
        this.dbType = dbType;
    }
}
