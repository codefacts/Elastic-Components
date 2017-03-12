package elasta.orm.nm.delete.dependency;

import elasta.orm.nm.upsert.TableData;

import java.util.Collection;

/**
 * Created by sohan on 3/12/2017.
 */
public interface TableToTableDataMap {
    Collection<TableData> getAsCollection(String dependentTable);
}
