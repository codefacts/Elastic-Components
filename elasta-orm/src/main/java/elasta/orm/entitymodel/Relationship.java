package elasta.orm.entitymodel;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
final public class Relationship {
    final Type type;
    final Name name;
    final String entity;

    public Relationship(Type type, Name name, String entity) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(name);
        Objects.requireNonNull(entity);
        this.type = type;
        this.name = name;
        this.entity = entity;
    }

    public Type getType() {
        return type;
    }

    public Name getName() {
        return name;
    }

    public String getEntity() {
        return entity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Relationship that = (Relationship) o;

        if (type != that.type) return false;
        if (name != that.name) return false;
        return entity != null ? entity.equals(that.entity) : that.entity == null;

    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (entity != null ? entity.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Relationship{" +
            "type=" + type +
            ", name=" + name +
            ", entity='" + entity + '\'' +
            '}';
    }

    public enum Name {
        HAS_ONE, HAS_MANY
    }

    public enum Type {
        ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY
    }
}
