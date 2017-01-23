package elasta.orm.nm.entitymodel;

import elasta.orm.json.core.RelationTable;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by Jango on 2017-01-08.
 */
final public class Field {
    final String name;
    final JavaType javaType;
    final Optional<Relationship> relationship;

    public Field(String name, JavaType javaType) {
        this(name, javaType, Optional.empty());
    }

    public Field(String name, JavaType javaType, Optional<Relationship> relationship) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(relationship);
        this.name = name;
        this.javaType = javaType;
        this.relationship = relationship;
    }

    public String getName() {
        return name;
    }

    public JavaType getJavaType() {
        return javaType;
    }

    public Optional<Relationship> getRelationship() {
        return relationship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (name != null ? !name.equals(field.name) : field.name != null) return false;
        if (javaType != field.javaType) return false;
        return relationship != null ? relationship.equals(field.relationship) : field.relationship == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (javaType != null ? javaType.hashCode() : 0);
        result = 31 * result + (relationship != null ? relationship.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Field{" +
            "name='" + name + '\'' +
            ", javaType=" + javaType +
            ", relationship=" + relationship +
            '}';
    }
}
