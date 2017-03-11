package elasta.orm.nm.delete.dependency;

import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.upsert.TableData;

import java.util.List;
import java.util.Map;

/**
 * Created by sohan on 3/11/2017.
 */
public interface IndirectDependencyDeleteHandler {
    void delete(TableData tableData, DeleteContext context);
}
