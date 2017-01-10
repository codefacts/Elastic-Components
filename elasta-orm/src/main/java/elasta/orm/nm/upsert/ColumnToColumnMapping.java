package elasta.orm.nm.upsert;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-09.
 */
final public class ColumnToColumnMapping {
    final String srcColumn;
    final String dstColumn;

    public ColumnToColumnMapping(String srcColumn, String dstColumn) {
        Objects.requireNonNull(srcColumn);
        Objects.requireNonNull(dstColumn);
        this.srcColumn = srcColumn;
        this.dstColumn = dstColumn;
    }

    public String getSrcColumn() {
        return srcColumn;
    }

    public String getDstColumn() {
        return dstColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColumnToColumnMapping that = (ColumnToColumnMapping) o;

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
        return "ColumnToColumnMapping{" +
            "srcColumn='" + srcColumn + '\'' +
            ", dstColumn='" + dstColumn + '\'' +
            '}';
    }
}
