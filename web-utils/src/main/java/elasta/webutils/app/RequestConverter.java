package elasta.webutils.app;

import elasta.core.intfs.Fun1Unckd;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by Jango on 11/6/2016.
 */
public interface RequestConverter<R> extends Fun1Unckd<RoutingContext, R> {
}
