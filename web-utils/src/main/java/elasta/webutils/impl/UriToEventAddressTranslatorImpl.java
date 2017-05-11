package elasta.webutils.impl;

import elasta.webutils.UriToEventAddressMap;
import elasta.webutils.UriToEventAddressTranslator;
import elasta.webutils.exceptions.UriToEventAddressTranslationException;
import elasta.webutils.model.UriAndHttpMethodPair;

import java.util.Objects;

/**
 * Created by Jango on 11/7/2016.
 */
final public class UriToEventAddressTranslatorImpl implements UriToEventAddressTranslator {
    final UriToEventAddressMap uriToEventAddressMap;

    public UriToEventAddressTranslatorImpl(UriToEventAddressMap uriToEventAddressMap) {
        Objects.requireNonNull(uriToEventAddressMap);
        this.uriToEventAddressMap = uriToEventAddressMap;
    }

    @Override
    public String apply(UriAndHttpMethodPair uriAndHttpMethodPair) throws Throwable {
        return getAddress(uriAndHttpMethodPair);
    }

    private String getAddress(UriAndHttpMethodPair uriAndHttpMethodPair) {
        final String address = uriToEventAddressMap.getMap().get(uriAndHttpMethodPair);
        if (address == null) {
            throw new UriToEventAddressTranslationException("No event address found for '" + uriAndHttpMethodPair + "'");
        }
        return address;
    }
}
