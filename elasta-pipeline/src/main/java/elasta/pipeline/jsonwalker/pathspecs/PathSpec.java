package elasta.pipeline.jsonwalker.pathspecs;

import lombok.Builder;
import lombok.Value;

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
