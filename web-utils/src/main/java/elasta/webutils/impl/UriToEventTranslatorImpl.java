package elasta.webutils.impl;

import elasta.webutils.UriToEventTranslator;
import elasta.webutils.exceptions.WebException;
import io.vertx.ext.web.RoutingContext;

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
