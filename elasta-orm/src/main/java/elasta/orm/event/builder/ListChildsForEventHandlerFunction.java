package elasta.orm.event.builder;

import java.util.List;

/**
 * Created by sohan on 3/29/2017.
 */
public interface ListChildsForEventHandlerFunction {
    List<ChildField> listChildFields(String entity);
}
