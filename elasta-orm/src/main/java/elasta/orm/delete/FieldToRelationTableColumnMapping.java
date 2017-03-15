package elasta.orm.delete;

import java.util.Objects;

/**
 * Created by Jango on 17/02/07.
 */
final public class FieldToRelationTableColumnMapping {
    final String srcField;
    final String relationColumn;

    public FieldToRelationTableColumnMapping(String srcField, String relationColumn) {
        Objects.requireNonNull(srcField);
        Objects.requireNonNull(relationColumn);
        this.srcField = srcField;
        this.relationColumn = relationColumn;
    }

    public String getSrcField() {
        return srcField;
    }

    public String getRelationColumn() {
        return relationColumn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FieldToRelationTableColumnMapping that = (FieldToRelationTableColumnMapping) o;

        if (srcField != null ? !srcField.equals(that.srcField) : that.srcField != null) return false;
        return relationColumn != null ? relationColumn.equals(that.relationColumn) : that.relationColumn == null;

    }

    @Override
    public int hashCode() {
        int result = srcField != null ? srcField.hashCode() : 0;
        result = 31 * result + (relationColumn != null ? relationColumn.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "FieldToRelationTableColumnMapping{" +
            "srcField='" + srcField + '\'' +
            ", relationColumn='" + relationColumn + '\'' +
            '}';
    }
}
