package elasta.composer.model.response.builder;

import elasta.composer.model.PageRequest;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/13/2017.
 */
public interface PageModelBuilder {

    JsonObject build(BuildParams params);

    @Value
    @Builder
    final class BuildParams {
        final List content;
        final PageRequest pageRequest;
        final long totalElementsCount;

        BuildParams(List content, PageRequest pageRequest, long totalElementsCount) {
            Objects.requireNonNull(content);
            Objects.requireNonNull(pageRequest);
            this.content = content;
            this.pageRequest = pageRequest;
            this.totalElementsCount = totalElementsCount;
        }
    }
}
