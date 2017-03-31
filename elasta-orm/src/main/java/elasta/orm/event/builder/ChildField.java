package elasta.orm.event.builder;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/29/2017.
 */
@Value
@Builder
final public class ChildField {
    final String field;
    final String referencingEntity;
    final FieldType fieldType;

    public ChildField(String field, String referencingEntity, FieldType fieldType) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(referencingEntity);
        Objects.requireNonNull(fieldType);
        this.field = field;
        this.referencingEntity = referencingEntity;
        this.fieldType = fieldType;
    }

    enum FieldType {
        Object, Array
    }
}
