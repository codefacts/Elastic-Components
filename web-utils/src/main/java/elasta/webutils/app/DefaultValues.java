package elasta.webutils.app;

import com.google.common.collect.ImmutableMap;
import io.vertx.core.http.HttpMethod;

import java.util.Map;

/**
 * Created by Jango on 11/9/2016.
 */
public interface DefaultValues {

    String apiPrefix();

    Map<HttpMethod, String> httpMethodToActionMap();
}
