package elasta.orm.entity;

import elasta.orm.entity.core.Entity;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
@FunctionalInterface
public interface EntitiesValidator {
    void validate(Params params);

    final class Params {
        final Collection<Entity> entities;
        final Map<String, TableDependency> tableToTableDependencyMap;
        final Map<String, Entity> entityNameToEntityMap;

        public Params(Collection<Entity> entities, Map<String, TableDependency> tableToTableDependencyMap, Map<String, Entity> entityNameToEntityMap) {
            Objects.requireNonNull(entities);
            Objects.requireNonNull(tableToTableDependencyMap);
            Objects.requireNonNull(entityNameToEntityMap);
            this.entities = entities;
            this.tableToTableDependencyMap = tableToTableDependencyMap;
            this.entityNameToEntityMap = entityNameToEntityMap;
        }

        public Collection<Entity> getEntities() {
            return entities;
        }

        public Map<String, TableDependency> getTableToTableDependencyMap() {
            return tableToTableDependencyMap;
        }

        public Map<String, Entity> getEntityNameToEntityMap() {
            return entityNameToEntityMap;
        }
    }

    final class ParamsBuilder {
        private Collection<Entity> entities;
        private Map<String, TableDependency> tableToTableDependencyMap;
        private Map<String, Entity> entityNameToEntityMap;

        public ParamsBuilder setEntities(Collection<Entity> entities) {
            this.entities = entities;
            return this;
        }

        public ParamsBuilder setTableToTableDependencyMap(Map<String, TableDependency> tableToTableDependencyMap) {
            this.tableToTableDependencyMap = tableToTableDependencyMap;
            return this;
        }

        public ParamsBuilder setEntityNameToEntityMap(Map<String, Entity> entityNameToEntityMap) {
            this.entityNameToEntityMap = entityNameToEntityMap;
            return this;
        }

        public Params createParams() {
            return new Params(entities, tableToTableDependencyMap, entityNameToEntityMap);
        }
    }
}
