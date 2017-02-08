package elasta.orm.nm.delete;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class IndirectDeleteDependency {
    private final String field;
    private final IndirectDeleteHandler deleteHandler;

    public IndirectDeleteDependency(String field, IndirectDeleteHandler deleteHandler) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(deleteHandler);
        this.field = field;
        this.deleteHandler = deleteHandler;
    }

    public String getField() {
        return field;
    }

    public IndirectDeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndirectDeleteDependency that = (IndirectDeleteDependency) o;

        if (field != null ? !field.equals(that.field) : that.field != null) return false;
        return deleteHandler != null ? deleteHandler.equals(that.deleteHandler) : that.deleteHandler == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (deleteHandler != null ? deleteHandler.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "IndirectDeleteDependency{" +
            "field='" + field + '\'' +
            ", deleteHandler=" + deleteHandler +
            '}';
    }
}
