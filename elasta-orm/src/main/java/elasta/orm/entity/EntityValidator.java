package elasta.orm.entity;

import elasta.orm.entity.core.Entity;
import elasta.orm.entity.impl.TableDependency;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
public interface EntityValidator {
    void validate(Params params);

    class Params {
        final Entity entity;
        final Map<String, TableDependency> tableToTabledependencyMap;
        final Map<String, Entity> entityNameToEntityMap;

        public Params(Entity entity, Map<String, TableDependency> tableToTabledependencyMap, Map<String, Entity> entityNameToEntityMap) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(tableToTabledependencyMap);
            Objects.requireNonNull(entityNameToEntityMap);
            this.entity = entity;
            this.tableToTabledependencyMap = tableToTabledependencyMap;
            this.entityNameToEntityMap = entityNameToEntityMap;
        }

        public Entity getEntity() {
            return entity;
        }

        public Map<String, TableDependency> getTableToTabledependencyMap() {
            return tableToTabledependencyMap;
        }

        public Map<String, Entity> getEntityNameToEntityMap() {
            return entityNameToEntityMap;
        }
    }

    class ParamsBuilder {
        private Entity entity;
        private Map<String, TableDependency> dependencyMap;
        private Map<String, Entity> entityNameToEntityMap;

        public ParamsBuilder setEntity(Entity entity) {
            this.entity = entity;
            return this;
        }

        public ParamsBuilder setDependencyMap(Map<String, TableDependency> dependencyMap) {
            this.dependencyMap = dependencyMap;
            return this;
        }

        public ParamsBuilder setEntityNameToEntityMap(Map<String, Entity> entityNameToEntityMap) {
            this.entityNameToEntityMap = entityNameToEntityMap;
            return this;
        }

        public Params createParams() {
            return new Params(entity, dependencyMap, entityNameToEntityMap);
        }
    }
}
