package elasta.orm.builder;

import elasta.orm.BaseOrm;
import elasta.orm.entity.core.Entity;

import java.util.Collection;

/**
 * Created by sohan on 3/19/2017.
 */
public interface BaseOrmBuilder {
    BaseOrm build(Collection<Entity> entities);
}
