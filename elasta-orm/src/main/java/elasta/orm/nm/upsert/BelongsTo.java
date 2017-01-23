package elasta.orm.nm.upsert;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
final public class BelongsTo {
    final String field;
    final BelongToHandler belongToHandler;
    final DependencyColumnValuePopulator dependencyColumnValuePopulator;

    public BelongsTo(String field, BelongToHandler belongToHandler, DependencyColumnValuePopulator dependencyColumnValuePopulator) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(belongToHandler);
        Objects.requireNonNull(dependencyColumnValuePopulator);
        this.field = field;
        this.belongToHandler = belongToHandler;
        this.dependencyColumnValuePopulator = dependencyColumnValuePopulator;
    }

    public String getField() {
        return field;
    }

    public BelongToHandler getBelongToHandler() {
        return belongToHandler;
    }

    public DependencyColumnValuePopulator getDependencyColumnValuePopulator() {
        return dependencyColumnValuePopulator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BelongsTo belongsTo = (BelongsTo) o;

        if (field != null ? !field.equals(belongsTo.field) : belongsTo.field != null) return false;
        return belongToHandler != null ? belongToHandler.equals(belongsTo.belongToHandler) : belongsTo.belongToHandler == null;

    }

    @Override
    public int hashCode() {
        int result = field != null ? field.hashCode() : 0;
        result = 31 * result + (belongToHandler != null ? belongToHandler.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BelongsTo{" +
            "fieldName='" + field + '\'' +
            ", belongToHandler=" + belongToHandler +
            '}';
    }
}
