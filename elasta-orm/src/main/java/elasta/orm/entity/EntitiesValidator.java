package elasta.orm.entity;

import elasta.orm.entity.core.Entity;

import java.util.Collection;

/**
 * Created by sohan on 3/17/2017.
 */
@FunctionalInterface
public interface EntitiesValidator {
    void validate(Collection<Entity> entities);
}
