package elasta.orm.nm.upsert;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-10.
 */
final public class ForeignColumnMapping {
    final String dstColumn;
    final String srcColumn;

    public ForeignColumnMapping(String srcColumn, String dstColumn) {
        Objects.requireNonNull(dstColumn);
        Objects.requireNonNull(srcColumn);
        this.dstColumn = dstColumn;
        this.srcColumn = srcColumn;
    }

    public String getDstColumn() {
        return dstColumn;
    }

    public String getSrcColumn() {
        return srcColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForeignColumnMapping that = (ForeignColumnMapping) o;

        if (dstColumn != null ? !dstColumn.equals(that.dstColumn) : that.dstColumn != null)
            return false;
        return srcColumn != null ? srcColumn.equals(that.srcColumn) : that.srcColumn == null;

    }

    @Override
    public int hashCode() {
        int result = dstColumn != null ? dstColumn.hashCode() : 0;
        result = 31 * result + (srcColumn != null ? srcColumn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ForeignColumnMapping{" +
            "foreignColumn='" + dstColumn + '\'' +
            ", column='" + srcColumn + '\'' +
            '}';
    }
}
