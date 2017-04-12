package elasta.orm.relation.delete.impl;

import elasta.orm.relation.delete.DeleteChildRelationsContext;
import elasta.sql.core.DeleteRelationData;

import java.util.Objects;
import java.util.Set;

/**
 * Created by sohan on 4/8/2017.
 */
final public class DeleteChildRelationsContextImpl implements DeleteChildRelationsContext {
    final Set<DeleteRelationData> dataSet;

    public DeleteChildRelationsContextImpl(Set<DeleteRelationData> dataSet) {
        Objects.requireNonNull(dataSet);
        this.dataSet = dataSet;
    }

    @Override
    public DeleteChildRelationsContext add(DeleteRelationData deleteRelationData) {
        dataSet.add(deleteRelationData);
        return this;
    }
}
