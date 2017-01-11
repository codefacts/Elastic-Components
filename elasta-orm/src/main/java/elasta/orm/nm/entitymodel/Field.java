package elasta.orm.nm.entitymodel;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-08.
 */
final public class Field {
    final String name;
    final FieldType fieldType;
    final Relationship relationship;

    public Field(String name, FieldType fieldType, Relationship relationship) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(fieldType);
        Objects.requireNonNull(relationship);
        this.name = name;
        this.fieldType = fieldType;
        this.relationship = relationship;
    }

    public String getName() {
        return name;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (name != null ? !name.equals(field.name) : field.name != null) return false;
        return fieldType == field.fieldType;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Field{" +
            "name='" + name + '\'' +
            ", fieldType=" + fieldType +
            '}';
    }
}
