package elasta.orm.entity.core;

import elasta.orm.entity.core.columnmapping.DbColumnMapping;
import lombok.Builder;
import lombok.Value;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-08.
 */
@Value
@Builder
final public class DbMapping {
    final String table;
    final String primaryColumn;
    final DbColumnMapping[] dbColumnMappings;

    public DbMapping(String table, String primaryColumn, DbColumnMapping[] dbColumnMappings) {
        Objects.requireNonNull(table);
        Objects.requireNonNull(primaryColumn);
        Objects.requireNonNull(dbColumnMappings);
        this.table = table;
        this.primaryColumn = primaryColumn;
        this.dbColumnMappings = dbColumnMappings;
    }

    public String getTable() {
        return table;
    }

    public String getPrimaryColumn() {
        return primaryColumn;
    }

    public DbColumnMapping[] getDbColumnMappings() {
        return dbColumnMappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbMapping dbMapping = (DbMapping) o;

        return table != null ? table.equals(dbMapping.table) : dbMapping.table == null;

    }

    @Override
    public int hashCode() {
        return table != null ? table.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "DbMapping{" +
            "dependentTable='" + table + '\'' +
            ", primaryColumn='" + primaryColumn + '\'' +
            ", dbColumnMappings=" + Arrays.toString(dbColumnMappings) +
            '}';
    }
}
