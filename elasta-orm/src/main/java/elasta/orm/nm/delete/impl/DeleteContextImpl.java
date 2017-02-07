package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.delete.DeleteData;

import java.util.LinkedHashSet;
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
