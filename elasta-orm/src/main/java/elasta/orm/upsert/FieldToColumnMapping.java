package elasta.orm.upsert;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-10.
 */
final public class FieldToColumnMapping {
    final String field;
    final String column;

    public FieldToColumnMapping(String field, String column) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(column);
        this.field = field;
        this.column = column;
    }

    public String getField() {
        return field;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldToColumnMapping that = (FieldToColumnMapping) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return column != null ? column.equals(that.column) : that.column == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (column != null ? column.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FieldToColumnMapping{" +
            "column='" + field + '\'' +
            ", column='" + column + '\'' +
            '}';
    }
}
