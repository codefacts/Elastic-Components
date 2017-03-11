package elasta.orm.nm.delete.dependency;

import elasta.orm.nm.upsert.TableData;

/**
 * Created by sohan on 3/12/2017.
 */
public interface ListTablesToDeleteContext {
    void add(TableData tableData);
}
