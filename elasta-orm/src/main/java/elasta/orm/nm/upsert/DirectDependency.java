package elasta.orm.nm.upsert;

/**
 * Created by Jango on 2017-01-09.
 */
final public class DirectDependency {
    final String fieldName;
    final DirectDependencyHandler dependencyHandler;
    final DependencyColumnValuePopulator dependencyColumnValuePopulator;

    public DirectDependency(String fieldName, DirectDependencyHandler dependencyHandler, DependencyColumnValuePopulator dependencyColumnValuePopulator) {
        this.fieldName = fieldName;
        this.dependencyHandler = dependencyHandler;
        this.dependencyColumnValuePopulator = dependencyColumnValuePopulator;
    }

    public String getFieldName() {
        return fieldName;
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

        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
        if (dependencyHandler != null ? !dependencyHandler.equals(that.dependencyHandler) : that.dependencyHandler != null)
            return false;
        return dependencyColumnValuePopulator != null ? dependencyColumnValuePopulator.equals(that.dependencyColumnValuePopulator) : that.dependencyColumnValuePopulator == null;

    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (dependencyHandler != null ? dependencyHandler.hashCode() : 0);
        result = 31 * result + (dependencyColumnValuePopulator != null ? dependencyColumnValuePopulator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DirectDependency{" +
            "fieldName='" + fieldName + '\'' +
            ", dependencyHandler=" + dependencyHandler +
            ", dependencyColumnValuePopulator=" + dependencyColumnValuePopulator +
            '}';
    }
}
