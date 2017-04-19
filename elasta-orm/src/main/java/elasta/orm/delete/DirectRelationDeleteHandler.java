package elasta.orm.delete;

import elasta.orm.upsert.TableData;

/**
 * Created by sohan on 4/18/2017.
 */
public interface DirectRelationDeleteHandler {
    void deleteRelation(TableData tableData, DeleteContext context);
}
