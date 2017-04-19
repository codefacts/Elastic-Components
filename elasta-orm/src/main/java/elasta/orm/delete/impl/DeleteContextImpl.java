package elasta.orm.delete.impl;

import elasta.orm.delete.DeleteContext;

import java.util.Objects;
import java.util.Set;

/**
 * Created by Jango on 17/02/07.
 */
final public class DeleteContextImpl implements DeleteContext {
    final Set<DeleteData> deleteDataSet;

    public DeleteContextImpl(Set<DeleteData> deleteDataSet) {
        Objects.requireNonNull(deleteDataSet);
        this.deleteDataSet = deleteDataSet;
    }

    @Override
    public DeleteContext add(final DeleteData deleteData) {

        if (deleteDataSet.contains(deleteData)) {
            return this;
        }

        deleteDataSet.add(deleteData);

        return this;
    }
}
