package elasta.orm.nm.upsert;

/**
 * Created by Jango on 2017-01-09.
 */
final public class IndirectDependency {
    final String fieldName;
    final IndirectDependencyHandler indirectDependencyHandler;
    final DependencyColumnValuePopulator dependencyColumnValuePopulator;

    public IndirectDependency(String fieldName, IndirectDependencyHandler indirectDependencyHandler, DependencyColumnValuePopulator dependencyColumnValuePopulator) {
        this.fieldName = fieldName;
        this.indirectDependencyHandler = indirectDependencyHandler;
        this.dependencyColumnValuePopulator = dependencyColumnValuePopulator;
    }

    public String getFieldName() {
        return fieldName;
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

        if (fieldName != null ? !fieldName.equals(that.fieldName) : that.fieldName != null) return false;
        if (indirectDependencyHandler != null ? !indirectDependencyHandler.equals(that.indirectDependencyHandler) : that.indirectDependencyHandler != null)
            return false;
        return dependencyColumnValuePopulator != null ? dependencyColumnValuePopulator.equals(that.dependencyColumnValuePopulator) : that.dependencyColumnValuePopulator == null;

    }

    @Override
    public int hashCode() {
        int result = fieldName != null ? fieldName.hashCode() : 0;
        result = 31 * result + (indirectDependencyHandler != null ? indirectDependencyHandler.hashCode() : 0);
        result = 31 * result + (dependencyColumnValuePopulator != null ? dependencyColumnValuePopulator.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IndirectDependency{" +
            "fieldName='" + fieldName + '\'' +
            ", indirectDependencyHandler=" + indirectDependencyHandler +
            ", dependencyColumnValuePopulator=" + dependencyColumnValuePopulator +
            '}';
    }
}
