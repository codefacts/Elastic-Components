package elasta.orm.entity.core;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-12.
 */
final public class ForeignColumnMapping {
    final String srcColumn;
    final String dstColumn;

    public ForeignColumnMapping(String srcColumn, String dstColumn) {
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

        ForeignColumnMapping that = (ForeignColumnMapping) o;

        if (!srcColumn.equals(that.srcColumn)) return false;
        return dstColumn.equals(that.dstColumn);
    }

    @Override
    public int hashCode() {
        int result = srcColumn.hashCode();
        result = 31 * result + dstColumn.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ForeignColumnMapping{" +
            "srcColumn='" + srcColumn + '\'' +
            ", dstColumn='" + dstColumn + '\'' +
            '}';
    }
}
