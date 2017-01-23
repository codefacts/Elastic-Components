package elasta.orm.nm.entitymodel;

import java.util.Arrays;
import java.util.Objects;

/**
 * Created by Jango on 2017-01-08.
 */
final public class Entity {
    final String name;
    final String primaryKey;
    final Field[] fields;
    final DbMapping dbMapping;

    public Entity(String name, String primaryKey, Field[] fields, DbMapping dbMapping) {
        this.dbMapping = dbMapping;
        Objects.requireNonNull(name);
        Objects.requireNonNull(primaryKey);
        Objects.requireNonNull(fields);
        this.name = name;
        this.primaryKey = primaryKey;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public Field[] getFields() {
        return fields;
    }

    public DbMapping getDbMapping() {
        return dbMapping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return name != null ? name.equals(entity.name) : entity.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Entity{" +
            "name='" + name + '\'' +
            ", primaryKey='" + primaryKey + '\'' +
            ", fields=" + Arrays.toString(fields) +
            '}';
    }
}
