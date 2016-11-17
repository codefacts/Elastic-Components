package elasta.webutils.app;

import elasta.core.intfs.Fun1Unckd;
import elasta.core.intfs.Fun2Unckd;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/6/2016.
 */
public interface UriToEventTranslator<T> extends Fun2Unckd<RoutingContext, T, String> {

}
