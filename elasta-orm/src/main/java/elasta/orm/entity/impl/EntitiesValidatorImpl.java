package elasta.orm.entity.impl;

import elasta.orm.entity.EntitiesValidator;
import elasta.orm.entity.EntityUtils;
import elasta.orm.entity.EntityValidator;
import elasta.orm.entity.core.Entity;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
final public class EntitiesValidatorImpl implements EntitiesValidator {
    final EntityValidator entityValidator;

    public EntitiesValidatorImpl(EntityValidator entityValidator) {
        Objects.requireNonNull(entityValidator);
        this.entityValidator = entityValidator;
    }

    @Override
    public void validate(Params params) {

        params.getEntities().forEach(
            entity -> entityValidator.validate(
                new EntityValidator.ParamsBuilder()
                    .setEntity(entity)
                    .setDependencyMap(
                        params.getTableToTableDependencyMap()
                    )
                    .setEntityNameToEntityMap(
                        params.getEntityNameToEntityMap()
                    )
                    .createParams()
            ));
    }
}
