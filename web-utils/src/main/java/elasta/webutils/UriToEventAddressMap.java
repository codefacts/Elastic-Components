package elasta.webutils;

import elasta.webutils.model.UriAndHttpMethodPair;

import java.util.Map;

/**
 * Created by sohan on 5/10/2017.
 */
public interface UriToEventAddressMap {

    Map<UriAndHttpMethodPair, String> getMap();

}
