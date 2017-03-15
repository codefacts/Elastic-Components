package elasta.orm.delete;

/**
 * Created by sohan on 3/12/2017.
 */
public interface TableToTableDeleteFunctionMap {
    DeleteFunction get(String table);
}
