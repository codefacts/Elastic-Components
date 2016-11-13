package elasta.webutils.app;

import com.google.common.collect.ImmutableMap;
import io.vertx.core.http.HttpMethod;

import java.util.Map;

/**
 * Created by Jango on 11/9/2016.
 */
public class DefaultValuesImpl implements DefaultValues {
    @Override
    public Map<HttpMethod, String> httpMethodToActionMap() {

        return ImmutableMap.of(
            HttpMethod.GET, "get",
            HttpMethod.POST, "create",
            HttpMethod.PUT, "save",
            HttpMethod.DELETE, "delete"
        );
    }
}
