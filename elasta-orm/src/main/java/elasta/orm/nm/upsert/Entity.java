package elasta.orm.nm.upsert;

import java.util.Arrays;

/**
 * Created by Jango on 2017-01-08.
 */
final public class Entity {
    final String name;
    final String primaryKey;
    final Field[] fields;

    public Entity(String name, String primaryKey, Field[] fields) {
        this.name = name;
        this.primaryKey = primaryKey;
        this.fields = fields;
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
