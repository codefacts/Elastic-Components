package elasta.composer.model.response.builder;

import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

/**
 * Created by sohan on 5/21/2017.
 */
public interface ValidationSuccessModelBuilder {

    JsonObject build(BuildParams params);

    @Value
    @Builder
    final class BuildParams {
    }
}
