package elasta.orm.dataflow.pathspecs;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/26/2017.
 */
@Value
@Builder
final public class JsonObjectPathSpecImpl implements JsonObjectPathSpec {
    final String field;

    public JsonObjectPathSpecImpl(String field) {
        Objects.requireNonNull(field);
        this.field = field;
    }

    @Override
    public String field() {
        return field;
    }

    @Override
    public Type type() {
        return Type.JsonObject;
    }
}
