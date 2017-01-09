package elasta.orm.nm.upsert;

/**
 * Created by Jango on 2017-01-09.
 */
final public class ColumnToColumnMapping {
    final String srcColumn;
    final String dstColumn;

    public ColumnToColumnMapping(String srcColumn, String dstColumn) {
        this.srcColumn = srcColumn;
        this.dstColumn = dstColumn;
    }

    public String getSrcColumn() {
        return srcColumn;
    }

    public String getDstColumn() {
        return dstColumn;
    }
}
