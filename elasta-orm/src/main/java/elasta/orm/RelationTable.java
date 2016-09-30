package elasta.orm;

/**
 * Created by Jango on 9/30/2016.
 */
public class RelationTable {
    private final String tableName;
    private final String firstColumn;
    private final String secondColumn;
    private final JavaType firstColumnType;
    private final JavaType secondColumnType;

    public RelationTable(String tableName, String firstColumn, String secondColumn, JavaType firstColumnType, JavaType secondColumnType) {
        this.tableName = tableName;
        this.firstColumn = firstColumn;
        this.secondColumn = secondColumn;
        this.firstColumnType = firstColumnType;
        this.secondColumnType = secondColumnType;
    }

    public String getTableName() {
        return tableName;
    }

    public String getFirstColumn() {
        return firstColumn;
    }

    public String getSecondColumn() {
        return secondColumn;
    }

    public JavaType getFirstColumnType() {
        return firstColumnType;
    }

    public JavaType getSecondColumnType() {
        return secondColumnType;
    }
}
