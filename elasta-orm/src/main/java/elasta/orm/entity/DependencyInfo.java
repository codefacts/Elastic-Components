package elasta.orm.entity;

import elasta.orm.entity.core.Entity;
import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.columnmapping.DbColumnMapping;

import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
final public class DependencyInfo {
    final Field field;
    final DbColumnMapping dbColumnMapping;

    public DependencyInfo(Field field, DbColumnMapping dbColumnMapping) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(dbColumnMapping);
        this.field = field;
        this.dbColumnMapping = dbColumnMapping;
    }

    public Field getField() {
        return field;
    }

    public DbColumnMapping getDbColumnMapping() {
        return dbColumnMapping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DependencyInfo that = (DependencyInfo) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return dbColumnMapping != null ? dbColumnMapping.equals(that.dbColumnMapping) : that.dbColumnMapping == null;
    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (dbColumnMapping != null ? dbColumnMapping.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DependencyInfo{" +
            "field=" + field +
            ", dbColumnMapping=" + dbColumnMapping +
            '}';
    }
}
