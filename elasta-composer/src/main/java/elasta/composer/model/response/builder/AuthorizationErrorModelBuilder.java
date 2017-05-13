package elasta.composer.model.response.builder;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

/**
 * Created by sohan on 5/14/2017.
 */
public interface AuthorizationErrorModelBuilder {

    JsonObject build(BuildParams params);

    @Value
    @Builder
    final class BuildParams {
    }
}
