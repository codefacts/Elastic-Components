package elasta.webutils.impl;

import com.google.common.collect.ImmutableMap;
import elasta.webutils.DefaultValues;
import io.vertx.core.http.HttpMethod;

import java.util.Map;

/**
 * Created by Jango on 11/9/2016.
 */
public class DefaultValuesImpl implements DefaultValues {

    private static final String API = "API".toLowerCase();

    @Override
    public String apiPrefix() {
        return API;
    }

    @Override
    public Map<HttpMethod, String> httpMethodToActionMap() {

        return ImmutableMap.of(
            HttpMethod.GET, "find",
            HttpMethod.POST, "create",
            HttpMethod.PUT, "update",
            HttpMethod.PATCH, "update",
            HttpMethod.DELETE, "delete"
        );
    }
}
