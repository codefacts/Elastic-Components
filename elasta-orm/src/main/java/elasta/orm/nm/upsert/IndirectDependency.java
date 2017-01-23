package elasta.orm.nm.upsert;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
final public class IndirectDependency {
    final String field;
    final IndirectDependencyHandler indirectDependencyHandler;
    final DependencyColumnValuePopulator dependencyColumnValuePopulator;

    public IndirectDependency(String field, IndirectDependencyHandler indirectDependencyHandler, DependencyColumnValuePopulator dependencyColumnValuePopulator) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(indirectDependencyHandler);
        Objects.requireNonNull(dependencyColumnValuePopulator);
        this.field = field;
        this.indirectDependencyHandler = indirectDependencyHandler;
        this.dependencyColumnValuePopulator = dependencyColumnValuePopulator;
    }

    public String getField() {
        return field;
    }

    public IndirectDependencyHandler getIndirectDependencyHandler() {
        return indirectDependencyHandler;
    }

    public DependencyColumnValuePopulator getDependencyColumnValuePopulator() {
        return dependencyColumnValuePopulator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndirectDependency that = (IndirectDependency) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        if (indirectDependencyHandler != null ? !indirectDependencyHandler.equals(that.indirectDependencyHandler) : that.indirectDependencyHandler != null)
            return false;
        return dependencyColumnValuePopulator != null ? dependencyColumnValuePopulator.equals(that.dependencyColumnValuePopulator) : that.dependencyColumnValuePopulator == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (indirectDependencyHandler != null ? indirectDependencyHandler.hashCode() : 0);
        result = 31 * result + (dependencyColumnValuePopulator != null ? dependencyColumnValuePopulator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IndirectDependency{" +
            "fieldName='" + field + '\'' +
            ", indirectDependencyHandler=" + indirectDependencyHandler +
            ", dependencyColumnValuePopulator=" + dependencyColumnValuePopulator +
            '}';
    }
}
