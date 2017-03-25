package elasta.orm.delete;

import java.util.Map;

/**
 * Created by sohan on 3/25/2017.
 */
public interface TableToDeleteTableFunctionMap {
    DeleteTableFunction get(String table);
}
