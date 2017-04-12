package elasta.orm.relation.delete;

import elasta.sql.core.DeleteRelationData;

/**
 * Created by sohan on 4/8/2017.
 */
public interface DeleteChildRelationsContext {
    DeleteChildRelationsContext add(DeleteRelationData deleteRelationData);
}
