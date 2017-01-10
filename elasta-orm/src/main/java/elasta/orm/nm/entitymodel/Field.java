package elasta.orm.nm.entitymodel;

/**
 * Created by Jango on 2017-01-08.
 */
final public class Field {
    final String name;
    final FieldType fieldType;

    public Field(String name, FieldType fieldType) {
        this.name = name;
        this.fieldType = fieldType;
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
