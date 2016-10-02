package elasta.orm.json.core;

import elasta.orm.json.sql.core.JavaType;

public class RelationTableBuilder {
    private String tableName;
    private String tableNameAlias;
    private String leftColumn;
    private String rightColumn;
    private JavaType leftColumnType;
    private JavaType rightColumnType;

    public RelationTableBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public RelationTableBuilder setTableNameAlias(String tableNameAlias) {
        this.tableNameAlias = tableNameAlias;
        return this;
    }

    public RelationTableBuilder setLeftColumn(String leftColumn) {
        this.leftColumn = leftColumn;
        return this;
    }

    public RelationTableBuilder setRightColumn(String rightColumn) {
        this.rightColumn = rightColumn;
        return this;
    }

    public RelationTableBuilder setLeftColumnType(JavaType leftColumnType) {
        this.leftColumnType = leftColumnType;
        return this;
    }

    public RelationTableBuilder setRightColumnType(JavaType rightColumnType) {
        this.rightColumnType = rightColumnType;
        return this;
    }

    public RelationTable createRelationTable() {
        return new RelationTable(tableName, tableNameAlias, leftColumn, rightColumn, leftColumnType, rightColumnType);
    }
}