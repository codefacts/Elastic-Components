package elasta.orm.query.read.impl;

import java.util.Objects;

/**
 * Created by Jango on 17/02/12.
 */
final public class IndirectRelation {
    final String field;
    final IndirectRelationReader indirectRelationReader;

    public IndirectRelation(String field, IndirectRelationReader indirectRelationReader) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(indirectRelationReader);
        this.field = field;
        this.indirectRelationReader = indirectRelationReader;
    }

    public String getField() {
        return field;
    }

    public IndirectRelationReader getIndirectRelationReader() {
        return indirectRelationReader;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndirectRelation that = (IndirectRelation) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return indirectRelationReader != null ? indirectRelationReader.equals(that.indirectRelationReader) : that.indirectRelationReader == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (indirectRelationReader != null ? indirectRelationReader.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IndirectRelation{" +
            "column='" + field + '\'' +
            ", indirectRelationReader=" + indirectRelationReader +
            '}';
    }
}
