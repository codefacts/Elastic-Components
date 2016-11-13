package elasta.webutils.app.impl;

import com.google.common.collect.ImmutableMap;
import elasta.webutils.app.DefaultValues;
import elasta.webutils.app.UriToEventTranslator;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;

import java.util.Map;

/**
 * Created by Jango on 11/7/2016.
 */
public class UriToEventTranslatorImpl implements UriToEventTranslator<JsonObject> {

    private final String PATH_SEPERATOR;
    private final Map<String, String> uriToEventMap;
    private final Map<HttpMethod, String> methodToActionMap;

    public UriToEventTranslatorImpl(String PATH_SEPERATOR, Map<String, String> uriToEventMap, DefaultValues defaultValues) {
        this.PATH_SEPERATOR = PATH_SEPERATOR;
        this.uriToEventMap = uriToEventMap;
        methodToActionMap = defaultValues.httpMethodToActionMap();
    }

    public UriToEventTranslatorImpl(String PATH_SEPERATOR, Map<String, String> uriToEventMap, Map<HttpMethod, String> methodToActionMap) {
        this.PATH_SEPERATOR = PATH_SEPERATOR;
        this.uriToEventMap = uriToEventMap;
        this.methodToActionMap = methodToActionMap;
    }

    public UriToEventTranslatorImpl(DefaultValues defaultValues) {
        uriToEventMap = ImmutableMap.of();
        PATH_SEPERATOR = ".";
        methodToActionMap = defaultValues.httpMethodToActionMap();
    }

    @Override
    public String apply(RequestInfo<JsonObject> requestInfo) throws Throwable {
        return null;
    }
}
