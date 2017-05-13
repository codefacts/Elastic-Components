package elasta.composer;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

/**
 * Created by sohan on 5/12/2017.
 */
public interface ContextHolder {

    ContextHolder setMap(ListMultimap<String, String> contextMap);

    ListMultimap<String, String> getMap();
}
