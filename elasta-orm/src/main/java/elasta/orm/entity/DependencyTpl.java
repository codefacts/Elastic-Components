package elasta.orm.entity;

import elasta.orm.entity.core.Entity;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
final public class DependencyTpl {
    final Entity entity;
    final Map<String, DependencyInfo> fieldToDependencyInfoMap;

    public DependencyTpl(Entity entity, Map<String, DependencyInfo> fieldToDependencyInfoMap) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(fieldToDependencyInfoMap);
        this.entity = entity;
        this.fieldToDependencyInfoMap = fieldToDependencyInfoMap;
    }

    public Entity getEntity() {
        return entity;
    }

    public Map<String, DependencyInfo> getFieldToDependencyInfoMap() {
        return fieldToDependencyInfoMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DependencyTpl that = (DependencyTpl) o;

        if (entity != null ? !entity.equals(that.entity) : that.entity != null) return false;
        return fieldToDependencyInfoMap != null ? fieldToDependencyInfoMap.equals(that.fieldToDependencyInfoMap) : that.fieldToDependencyInfoMap == null;
    }

    @Override
    public int hashCode() {
        int result = entity != null ? entity.hashCode() : 0;
        result = 31 * result + (fieldToDependencyInfoMap != null ? fieldToDependencyInfoMap.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DependencyTpl{" +
            "entity=" + entity +
            ", fieldToDependencyInfoMap=" + fieldToDependencyInfoMap +
            '}';
    }

    public void add(DependencyInfo dependencyInfo) {
        fieldToDependencyInfoMap.put(
            dependencyInfo.getField().getName(),
            dependencyInfo
        );
    }
}
