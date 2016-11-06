package elasta.webutils.app;

import elasta.core.intfs.Fun1Async;
import elasta.core.intfs.Fun1Unckd;
import elasta.core.intfs.Fun2Unckd;
import io.vertx.core.http.HttpMethod;

/**
 * Created by Jango on 11/6/2016.
 */
public interface UriToEventTranslator extends Fun2Unckd<String, HttpMethod, String> {

}
