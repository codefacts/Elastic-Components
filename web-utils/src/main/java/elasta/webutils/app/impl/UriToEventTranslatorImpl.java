package elasta.webutils.app.impl;

import com.google.common.collect.ImmutableMap;
import elasta.webutils.app.DefaultValues;
import elasta.webutils.app.UriToEventTranslator;
import elasta.webutils.app.exceptions.WebException;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Map;

/**
 * Created by Jango on 11/7/2016.
 */
public class UriToEventTranslatorImpl implements UriToEventTranslator<Object> {
    @Override
    public String apply(RoutingContext context, Object o) throws Throwable {
        String event = context.get("event");

        if (event == null || event.isEmpty()) {
            throw new WebException("No event mapping found for requested uri. URI: " + context.request().method() + " '" + context.request().uri() + "' -> EVENT: '" + event + "'");
        }

        return event;
    }
}
