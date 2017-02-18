package elasta.orm.nm.delete.impl;

import java.util.Objects;

/**
 * Created by Jango on 17/02/16.
 */
final public class PParentDeleteDependency {
    final PParentDeleteHandler deleteHandler;

    public PParentDeleteDependency(PParentDeleteHandler deleteHandler) {
        Objects.requireNonNull(deleteHandler);
        this.deleteHandler = deleteHandler;
    }

    public PParentDeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PParentDeleteDependency that = (PParentDeleteDependency) o;

        return deleteHandler != null ? deleteHandler.equals(that.deleteHandler) : that.deleteHandler == null;

    }

    @Override
    public int hashCode() {
        return deleteHandler != null ? deleteHandler.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PParentDeleteDependency{" +
            "deleteHandler=" + deleteHandler +
            '}';
    }
}
