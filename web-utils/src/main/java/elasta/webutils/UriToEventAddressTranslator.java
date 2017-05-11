package elasta.webutils;

import elasta.core.intfs.Fun1Unckd;
import elasta.webutils.model.UriAndHttpMethodPair;

/**
 * Created by Jango on 11/6/2016.
 */
public interface UriToEventAddressTranslator extends Fun1Unckd<UriAndHttpMethodPair, String> {

    @Override
    String apply(UriAndHttpMethodPair translationParams) throws Throwable;
}
