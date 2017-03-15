package elasta.orm.entitymodel;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
final public class Column {
    final String name;
    final DbType dbType;

    public Column(String name, DbType dbType) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(dbType);
        this.name = name;
        this.dbType = dbType;
    }

    public String getName() {
        return name;
    }

    public DbType getDbType() {
        return dbType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Column column = (Column) o;

        if (name != null ? !name.equals(column.name) : column.name != null) return false;
        return dbType == column.dbType;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (dbType != null ? dbType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Column{" +
            "name='" + name + '\'' +
            ", dbType=" + dbType +
            '}';
    }
}
