package elasta.orm.nm.delete.dependency;

import elasta.orm.nm.upsert.TableData;

import java.util.Objects;
import java.util.Set;

/**
 * Created by sohan on 3/12/2017.
 */
final public class ListTablesToDeleteContextImpl implements ListTablesToDeleteContext {
    final Set<TableData> tableDataSet;

    public ListTablesToDeleteContextImpl(Set<TableData> tableDataSet) {
        Objects.requireNonNull(tableDataSet);
        this.tableDataSet = tableDataSet;
    }

    @Override
    public void add(TableData tableData) {
        tableDataSet.add(tableData);
    }
}
