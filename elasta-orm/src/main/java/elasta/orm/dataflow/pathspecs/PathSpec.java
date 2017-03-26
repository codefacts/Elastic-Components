package elasta.orm.dataflow.pathspecs;

import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/26/2017.
 */
public interface PathSpec {

    String field();

    Type type();

    enum Type {
        JsonObject, JsonArray
    }
}
