package elasta.orm.nm.upsert;

import java.util.Arrays;

/**
 * Created by Jango on 2017-01-08.
 */
final public class DbMapping {
    final String table;
    final String primaryColumn;
    final DbColumnMapping[] dbColumnMappings;

    public DbMapping(String table, String primaryColumn, DbColumnMapping[] dbColumnMappings) {
        this.table = table;
        this.primaryColumn = primaryColumn;
        this.dbColumnMappings = dbColumnMappings;
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
