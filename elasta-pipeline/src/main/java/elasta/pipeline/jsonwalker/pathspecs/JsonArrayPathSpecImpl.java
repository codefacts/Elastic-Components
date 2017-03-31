package elasta.pipeline.jsonwalker.pathspecs;

import elasta.pipeline.jsonwalker.pathspecs.ex.PathSpecException;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/26/2017.
 */
@Value
@Builder
public class JsonArrayPathSpecImpl implements JsonArrayPathSpec {
    final String field;
    final int index;

    public JsonArrayPathSpecImpl(String field, int index) {
        Objects.requireNonNull(field);
        if (index < 0) {
            throw new PathSpecException("Invalid index " + index + " provided");
        }
        this.field = field;
        this.index = index;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public String field() {
        return field;
    }

    @Override
    public Type type() {
        return Type.JsonArray;
    }
}
