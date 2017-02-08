package elasta.orm.nm.delete;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class BelongsToDeleteDependency {
    private final BelongsToDeleteHandler deleteHandler;
    private final String field;

    public BelongsToDeleteDependency(BelongsToDeleteHandler deleteHandler, String field) {
        Objects.requireNonNull(deleteHandler);
        Objects.requireNonNull(field);
        this.deleteHandler = deleteHandler;
        this.field = field;
    }

    public BelongsToDeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    public String getField() {
        return field;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BelongsToDeleteDependency that = (BelongsToDeleteDependency) o;

        if (deleteHandler != null ? !deleteHandler.equals(that.deleteHandler) : that.deleteHandler != null)
            return false;
        return field != null ? field.equals(that.field) : that.field == null;

    }

    @Override
    public int hashCode() {
        int result = deleteHandler != null ? deleteHandler.hashCode() : 0;
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BelongsToDeleteDependency{" +
            "deleteHandler=" + deleteHandler +
            ", field='" + field + '\'' +
            '}';
    }
}
