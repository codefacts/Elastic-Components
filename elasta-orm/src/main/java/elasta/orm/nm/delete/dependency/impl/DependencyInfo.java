package elasta.orm.nm.delete.dependency.impl;

import elasta.orm.nm.entitymodel.columnmapping.DbColumnMapping;

import java.util.Objects;

/**
 * Created by sohan on 3/5/2017.
 */
final public class DependencyInfo {
    final String dependentTable;
    final DbColumnMapping dbColumnMapping;

    public DependencyInfo(String dependentTable, DbColumnMapping dbColumnMapping) {
        Objects.requireNonNull(dependentTable);
        Objects.requireNonNull(dbColumnMapping);
        this.dependentTable = dependentTable;
        this.dbColumnMapping = dbColumnMapping;
    }

    public String getDependentTable() {
        return dependentTable;
    }

    public DbColumnMapping getDbColumnMapping() {
        return dbColumnMapping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DependencyInfo that = (DependencyInfo) o;

        if (dependentTable != null ? !dependentTable.equals(that.dependentTable) : that.dependentTable != null)
            return false;
        return dbColumnMapping != null ? dbColumnMapping.equals(that.dbColumnMapping) : that.dbColumnMapping == null;
    }

    @Override
    public int hashCode() {
        int result = dependentTable != null ? dependentTable.hashCode() : 0;
        result = 31 * result + (dbColumnMapping != null ? dbColumnMapping.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DependencyInfo{" +
            "dependentTable='" + dependentTable + '\'' +
            ", dbColumnMapping=" + dbColumnMapping +
            '}';
    }
}
