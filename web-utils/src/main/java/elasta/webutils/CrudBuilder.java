package elasta.webutils;

import io.vertx.ext.web.Router;

/**
 * Created by Jango on 9/13/2016.
 */
@FunctionalInterface
public interface CrudBuilder {
    void addRoutesAndHandlers(Router router, String prefixUri, String resourceName);
}
