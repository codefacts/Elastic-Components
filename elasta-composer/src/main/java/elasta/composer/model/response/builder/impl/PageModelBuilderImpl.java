package elasta.composer.model.response.builder.impl;

import com.google.common.collect.ImmutableMap;
import elasta.composer.model.response.PageModel;
import elasta.composer.model.PageRequest;
import elasta.composer.model.response.builder.PageModelBuilder;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/13/2017.
 */
final public class PageModelBuilderImpl implements PageModelBuilder {
    @Override
    public JsonObject build(BuildParams params) {
        final PageRequest pageRequest = params.getPageRequest();
        return new JsonObject(
            ImmutableMap.of(
                PageModel.content, params.getContent(),
                PageModel.page, pageRequest.getPage(),
                PageModel.pageSize, pageRequest.getPageSize(),
                PageModel.totalElementsCount, params.getTotalElementsCount()
            )
        );
    }
}
