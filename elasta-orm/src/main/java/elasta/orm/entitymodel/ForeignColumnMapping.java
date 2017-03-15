package elasta.orm.entitymodel;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
final public class ForeignColumnMapping {
    final Column srcColumn;
    final Column dstColumn;

    public ForeignColumnMapping(Column srcColumn, Column dstColumn) {
        Objects.requireNonNull(srcColumn);
        Objects.requireNonNull(dstColumn);
        this.srcColumn = srcColumn;
        this.dstColumn = dstColumn;
    }

    public Column getSrcColumn() {
        return srcColumn;
    }

    public Column getDstColumn() {
        return dstColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignColumnMapping that = (ForeignColumnMapping) o;

        if (srcColumn != null ? !srcColumn.equals(that.srcColumn) : that.srcColumn != null) return false;
        return dstColumn != null ? dstColumn.equals(that.dstColumn) : that.dstColumn == null;

    }

    @Override
    public int hashCode() {
        int result = srcColumn != null ? srcColumn.hashCode() : 0;
        result = 31 * result + (dstColumn != null ? dstColumn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ForeignColumnMapping{" +
            "srcColumn=" + srcColumn +
            ", dstColumn=" + dstColumn +
            '}';
    }
}
