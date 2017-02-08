package elasta.orm.nm.delete;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class DirectDeleteDependency {
    private final String field;
    private final DirectDeleteHandler deleteHandler;

    public DirectDeleteDependency(String field, DirectDeleteHandler deleteHandler) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(deleteHandler);
        this.field = field;
        this.deleteHandler = deleteHandler;
    }

    public String getField() {
        return field;
    }

    public DirectDeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectDeleteDependency that = (DirectDeleteDependency) o;

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
        return "DirectDeleteDependency{" +
            "field='" + field + '\'' +
            ", deleteHandler=" + deleteHandler +
            '}';
    }
}
