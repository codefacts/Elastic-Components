package elasta.orm.json.core;

import elasta.orm.json.sql.core.JavaType;

/**
 * Created by Jango on 9/30/2016.
 */
public class RelationTable {
    private final String tableName;
    private final String tableNameAlias;
    private final String leftColumn;
    private final String rightColumn;
    private final JavaType leftColumnType;
    private final JavaType rightColumnType;

    public RelationTable(String tableName, String tableNameAlias, String leftColumn, String rightColumn, JavaType leftColumnType, JavaType rightColumnType) {
        this.tableName = tableName;
        this.tableNameAlias = tableNameAlias;
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
        this.leftColumnType = leftColumnType;
        this.rightColumnType = rightColumnType;
    }

    public String getTableName() {
        return tableName;
    }

    public String getLeftColumn() {
        return leftColumn;
    }

    public String getRightColumn() {
        return rightColumn;
    }

    public JavaType getLeftColumnType() {
        return leftColumnType;
    }

    public JavaType getRightColumnType() {
        return rightColumnType;
    }
}
