package elasta.webutils.app.impl;

import com.google.common.collect.ImmutableMap;
import elasta.webutils.app.UriToEventTranslator;
import io.vertx.core.http.HttpMethod;

import java.util.Map;

/**
 * Created by Jango on 11/7/2016.
 */
public class UriToEventTranslatorImpl implements UriToEventTranslator {
    private static final Map<HttpMethod, String> URI_TO_EVENT_MAP = ImmutableMap.of(
        HttpMethod.GET, "",
        HttpMethod.POST, "create",
        HttpMethod.PUT, "save",
        HttpMethod.DELETE, "delete"
    );
    private final String PATH_SEPERATOR;
    private final Map<String, String> uriToEventMap;
    private final Map<HttpMethod, String> methodToActionMap;

    public UriToEventTranslatorImpl(String PATH_SEPERATOR, Map<String, String> uriToEventMap) {
        this.PATH_SEPERATOR = PATH_SEPERATOR;
        this.uriToEventMap = uriToEventMap;
        methodToActionMap = URI_TO_EVENT_MAP;
    }

    public UriToEventTranslatorImpl(String PATH_SEPERATOR, Map<String, String> uriToEventMap, Map<HttpMethod, String> methodToActionMap) {
        this.PATH_SEPERATOR = PATH_SEPERATOR;
        this.uriToEventMap = uriToEventMap;
        this.methodToActionMap = methodToActionMap;
    }

    public UriToEventTranslatorImpl() {
        uriToEventMap = ImmutableMap.of();
        PATH_SEPERATOR = ".";
        methodToActionMap = URI_TO_EVENT_MAP;
    }

    @Override
    public String apply(String uri, HttpMethod httpMethod) throws Throwable {

        return uriToEventMap.get(uri) + PATH_SEPERATOR + methodToActionMap.get(httpMethod);
    }
}
