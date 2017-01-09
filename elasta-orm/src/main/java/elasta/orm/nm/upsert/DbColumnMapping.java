package elasta.orm.nm.upsert;

/**
 * Created by Jango on 2017-01-08.
 */
final public class DbColumnMapping {
    final String column;
    final String field;
    final DbType dbType;

    public DbColumnMapping(String column, String field, DbType dbType) {
        this.column = column;
        this.field = field;
        this.dbType = dbType;
    }

    public String getColumn() {
        return column;
    }

    public String getField() {
        return field;
    }

    public DbType getDbType() {
        return dbType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DbColumnMapping that = (DbColumnMapping) o;

        if (column != null ? !column.equals(that.column) : that.column != null) return false;
        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return dbType == that.dbType;

    }

    @Override
    public int hashCode() {
        int result = column != null ? column.hashCode() : 0;
        result = 31 * result + (field != null ? field.hashCode() : 0);
        result = 31 * result + (dbType != null ? dbType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DbColumnMapping{" +
            "column='" + column + '\'' +
            ", field='" + field + '\'' +
            ", dbType=" + dbType +
            '}';
    }
}
