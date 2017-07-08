package elasta.composer;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by sohan on 7/7/2017.
 */
public interface StateHandlersMap {

    <T extends MsgEnterEventHandlerP<P, R>, P, R> Optional<T> get(Class<T> tClass);

    Set<Class> keys();

    Map<Class, MsgEnterEventHandlerP> getMap();
}
