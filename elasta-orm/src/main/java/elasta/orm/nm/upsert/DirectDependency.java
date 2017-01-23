package elasta.orm.nm.upsert;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
final public class DirectDependency {
    final String field;
    final DirectDependencyHandler dependencyHandler;
    final DependencyColumnValuePopulator dependencyColumnValuePopulator;

    public DirectDependency(String field, DirectDependencyHandler dependencyHandler, DependencyColumnValuePopulator dependencyColumnValuePopulator) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(dependencyHandler);
        Objects.requireNonNull(dependencyColumnValuePopulator);
        this.field = field;
        this.dependencyHandler = dependencyHandler;
        this.dependencyColumnValuePopulator = dependencyColumnValuePopulator;
    }

    public String getField() {
        return field;
    }

    public DirectDependencyHandler getDependencyHandler() {
        return dependencyHandler;
    }

    public DependencyColumnValuePopulator getDependencyColumnValuePopulator() {
        return dependencyColumnValuePopulator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectDependency that = (DirectDependency) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        if (dependencyHandler != null ? !dependencyHandler.equals(that.dependencyHandler) : that.dependencyHandler != null)
            return false;
        return dependencyColumnValuePopulator != null ? dependencyColumnValuePopulator.equals(that.dependencyColumnValuePopulator) : that.dependencyColumnValuePopulator == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (dependencyHandler != null ? dependencyHandler.hashCode() : 0);
        result = 31 * result + (dependencyColumnValuePopulator != null ? dependencyColumnValuePopulator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DirectDependency{" +
            "fieldName='" + field + '\'' +
            ", dependencyHandler=" + dependencyHandler +
            ", dependencyColumnValuePopulator=" + dependencyColumnValuePopulator +
            '}';
    }
}
