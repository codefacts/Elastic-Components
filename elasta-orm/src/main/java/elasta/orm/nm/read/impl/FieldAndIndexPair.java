package elasta.orm.nm.read.impl;

import java.util.Objects;

/**
 * Created by Jango on 17/02/12.
 */
final public class FieldAndIndexPair {
    final String field;
    final int index;

    public FieldAndIndexPair(String field, int index) {
        Objects.requireNonNull(field);
        this.field = field;
        this.index = index;
    }

    public String getField() {
        return field;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldAndIndexPair that = (FieldAndIndexPair) o;

        if (index != that.index) return false;
        return field != null ? field.equals(that.field) : that.field == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return "FieldAndIndexPair{" +
            "column='" + field + '\'' +
            ", index=" + index +
            '}';
    }
}
