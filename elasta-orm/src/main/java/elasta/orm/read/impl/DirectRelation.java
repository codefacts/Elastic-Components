package elasta.orm.read.impl;

import java.util.Objects;

/**
 * Created by Jango on 17/02/12.
 */
final public class DirectRelation {
    final String field;
    final DirectRelationReader directRelationReader;

    public DirectRelation(String field, DirectRelationReader directRelationReader) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(directRelationReader);
        this.field = field;
        this.directRelationReader = directRelationReader;
    }

    public String getField() {
        return field;
    }

    public DirectRelationReader getDirectRelationReader() {
        return directRelationReader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectRelation that = (DirectRelation) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return directRelationReader != null ? directRelationReader.equals(that.directRelationReader) : that.directRelationReader == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (directRelationReader != null ? directRelationReader.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DirectRelation{" +
            "field='" + field + '\'' +
            ", directRelationReader=" + directRelationReader +
            '}';
    }
}
