package elasta.orm.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Entity;
import elasta.orm.impl.BaseOrmImpl;
import elasta.sql.SqlDB;
import lombok.Value;

import java.util.*;

/**
 * Created by sohan on 4/12/2017.
 */
final public class OperationMapBuilder {
    final Collection<Entity> entities;
    final EntityMappingHelper helper;
    final SqlDB sqlDB;

    public OperationMapBuilder(Collection<Entity> entities, EntityMappingHelper helper, SqlDB sqlDB) {
        Objects.requireNonNull(entities);
        Objects.requireNonNull(helper);
        Objects.requireNonNull(sqlDB);
        this.entities = entities;
        this.helper = helper;
        this.sqlDB = sqlDB;
    }

    public Map<String, BaseOrmImpl.EntityOperation> build() {
        return operationMap();
    }

    private Map<String, BaseOrmImpl.EntityOperation> operationMap() {

        ImmutableMap.Builder<String, BaseOrmImpl.EntityOperation> mapBuilder = ImmutableMap.builder();

        EntityOperationBuilder operationBuilder = new EntityOperationBuilder(helper, sqlDB);

        entities.stream()
            .map(operationBuilder::toEntityOperation)
            .forEach(
                entityAndEntityOperation -> mapBuilder
                    .put(entityAndEntityOperation.getEntity(), entityAndEntityOperation.getEntityOperation())
            );
        operationBuilder.makeAllBuilderContextsImmutable();
        return mapBuilder.build();
    }

    @Value
    static final class EntityAndEntityOperation {
        final String entity;
        final BaseOrmImpl.EntityOperation entityOperation;

        EntityAndEntityOperation(String entity, BaseOrmImpl.EntityOperation entityOperation) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(entityOperation);
            this.entity = entity;
            this.entityOperation = entityOperation;
        }
    }
}
