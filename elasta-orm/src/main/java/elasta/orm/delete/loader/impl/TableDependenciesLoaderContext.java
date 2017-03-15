package elasta.orm.delete.loader.impl;

import elasta.orm.upsert.TableData;

/**
 * Created by sohan on 3/12/2017.
 */
public interface TableDependenciesLoaderContext {

    boolean contains(TableData parentTableData);

    void add(TableData parentTableData);
}
