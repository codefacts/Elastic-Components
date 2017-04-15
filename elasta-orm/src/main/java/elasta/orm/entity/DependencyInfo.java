package elasta.orm.entity;

import elasta.orm.entity.core.Field;
import elasta.orm.entity.core.columnmapping.RelationMapping;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
@Data
@Builder
final public class DependencyInfo {
    final Field field;
    final RelationMapping relationMapping;

    public DependencyInfo(Field field, RelationMapping relationMapping) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(relationMapping);
        this.field = field;
        this.relationMapping = relationMapping;
    }
}
