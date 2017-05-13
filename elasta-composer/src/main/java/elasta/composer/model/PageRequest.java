package elasta.composer.model;

import com.google.common.collect.ImmutableMap;
import elasta.composer.model.response.PageModel;
import io.vertx.core.json.JsonObject;
import lombok.Builder;
import lombok.Value;

/**
 * Created by sohan on 5/13/2017.
 */
@Value
@Builder
public class PageRequest {
    final long page;
    final int pageSize;

    PageRequest(long page, int pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    JsonObject toJson() {
        return new JsonObject(
            ImmutableMap.of(
                PageModel.page, page,
                PageModel.pageSize, pageSize
            )
        );
    }
}
