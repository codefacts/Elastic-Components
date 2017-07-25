package elasta.orm.entity;

import elasta.orm.entity.core.Entity;
import elasta.orm.entity.impl.TableDependency;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
@FunctionalInterface
public interface EntitiesPreprocessor {
    List<Entity> process(Params params);

    @Value
    @Builder
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
    }
}
