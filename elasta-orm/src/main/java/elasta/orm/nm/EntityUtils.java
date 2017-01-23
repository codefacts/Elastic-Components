package elasta.orm.nm;

import com.google.common.collect.ImmutableMap;
import elasta.orm.nm.entitymodel.Entity;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Jango on 2017-01-21.
 */
public interface EntityUtils {
    static Map<String, Entity> toEntityNameToEntityMap(Collection<Entity> entities) {
        return ImmutableMap.copyOf(
            entities.stream().collect(
                Collectors.toMap(
                    Entity::getName,
                    e -> e
                )
            )
        );
    }
}
