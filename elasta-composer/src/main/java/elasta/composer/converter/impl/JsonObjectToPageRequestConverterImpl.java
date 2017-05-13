package elasta.composer.converter.impl;

import elasta.composer.converter.JsonObjectToPageRequestConverter;
import elasta.composer.model.PageRequest;
import elasta.composer.model.request.QueryModel;
import io.vertx.core.json.JsonObject;

/**
 * Created by sohan on 5/13/2017.
 */
final public class JsonObjectToPageRequestConverterImpl implements JsonObjectToPageRequestConverter {
    final long defaultPage;
    final int defaultPageSize;

    public JsonObjectToPageRequestConverterImpl(long defaultPage, int defaultPageSize) {
        this.defaultPage = defaultPage;
        this.defaultPageSize = defaultPageSize;
    }

    @Override
    public PageRequest convert(JsonObject query) {
        return PageRequest.builder()
            .page(query.getLong(QueryModel.page, defaultPage))
            .pageSize(query.getInteger(QueryModel.pageSize, defaultPageSize))
            .build();
    }
}
