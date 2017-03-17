package elasta.orm.entity;

import elasta.orm.entity.core.Entity;

import java.util.Collection;
import java.util.List;

/**
 * Created by sohan on 3/17/2017.
 */
@FunctionalInterface
public interface EntitiesPreprocessor {
    List<Entity> process(Collection<Entity> entities);
}
