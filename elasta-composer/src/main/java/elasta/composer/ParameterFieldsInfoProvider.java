package elasta.composer;

import elasta.pipeline.converter.Converter;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/25/2017.
 */
public interface ParameterFieldsInfoProvider {

    List<FieldInfo> get(String entity);

    @Value
    @Builder
    final class FieldInfo {
        final String field;
        final Converter converter;

        FieldInfo(String field, Converter converter) {
            Objects.requireNonNull(field);
            Objects.requireNonNull(converter);
            this.field = field;
            this.converter = converter;
        }
    }
}
