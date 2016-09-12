package elasta.composer;

import elasta.module.ExportScript;

/**
 * Created by Jango on 9/12/2016.
 */
public interface EventHandlers {
    String CREATE = "create";
    String UPDATE_ALL_PROPERTIES = "update-all-properties";
    String UPDATE_SOME_PROPERTIES = "update-some-properties";
    String FIND_ALL = "fina-all";
    String FIND = "find";
    String DELETE = "delete";
}