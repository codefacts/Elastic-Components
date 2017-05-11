package elasta.webutils.impl;

import elasta.webutils.UriToEventAddressMap;
import elasta.webutils.model.UriAndHttpMethodPair;

import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 5/10/2017.
 */
final public class UriToEventAddressMapImpl implements UriToEventAddressMap {
    final Map<UriAndHttpMethodPair, String> uriAndHttpMethodPairToEventAddressMap;

    public UriToEventAddressMapImpl(Map<UriAndHttpMethodPair, String> uriAndHttpMethodPairToEventAddressMap) {
        Objects.requireNonNull(uriAndHttpMethodPairToEventAddressMap);
        this.uriAndHttpMethodPairToEventAddressMap = uriAndHttpMethodPairToEventAddressMap;
    }

    @Override
    public Map<UriAndHttpMethodPair, String> getMap() {
        return uriAndHttpMethodPairToEventAddressMap;
    }
}
