package elasta.orm;

public class RelationTableBuilder {
    private String tableName;
    private String firstColumn;
    private String secondColumn;
    private JavaType firstColumnType;
    private JavaType secondColumnType;

    public RelationTableBuilder setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public RelationTableBuilder setFirstColumn(String firstColumn) {
        this.firstColumn = firstColumn;
        return this;
    }

    public RelationTableBuilder setSecondColumn(String secondColumn) {
        this.secondColumn = secondColumn;
        return this;
    }

    public RelationTableBuilder setFirstColumnType(JavaType firstColumnType) {
        this.firstColumnType = firstColumnType;
        return this;
    }

    public RelationTableBuilder setSecondColumnType(JavaType secondColumnType) {
        this.secondColumnType = secondColumnType;
        return this;
    }

    public RelationTable createRelationTable() {
        return new RelationTable(tableName, firstColumn, secondColumn, firstColumnType, secondColumnType);
    }
}