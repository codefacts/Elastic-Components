package elasta.orm.upsert;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
final public class IndirectDependency {
    final String field;
    final IndirectDependencyHandler indirectDependencyHandler;

    public IndirectDependency(String field, IndirectDependencyHandler indirectDependencyHandler) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(indirectDependencyHandler);
        this.field = field;
        this.indirectDependencyHandler = indirectDependencyHandler;
    }

    public String getField() {
        return field;
    }

    public IndirectDependencyHandler getIndirectDependencyHandler() {
        return indirectDependencyHandler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndirectDependency that = (IndirectDependency) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return indirectDependencyHandler != null ? indirectDependencyHandler.equals(that.indirectDependencyHandler) : that.indirectDependencyHandler == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (indirectDependencyHandler != null ? indirectDependencyHandler.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IndirectDependency{" +
            "field='" + field + '\'' +
            ", indirectDependencyHandler=" + indirectDependencyHandler +
            '}';
    }
}
