package elasta.orm.nm.entitymodel;

import elasta.orm.nm.entitymodel.columnmapping.DbColumnMapping;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-08.
 */
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
            "table='" + table + '\'' +
            ", primaryColumn='" + primaryColumn + '\'' +
            ", dbColumnMappings=" + Arrays.toString(dbColumnMappings) +
            '}';
    }
}
