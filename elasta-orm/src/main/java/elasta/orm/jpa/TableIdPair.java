package elasta.orm.jpa;

/**
 * Created by Jango on 10/7/2016.
 */
public class TableIdPair {
    private final String table;
    private final Object id;
    private final RelationTableInfo relationTableInfo;

    public TableIdPair(String table, Object id, RelationTableInfo relationTableInfo) {
        this.table = table;
        this.id = id;
        this.relationTableInfo = relationTableInfo;
    }

    public String getTable() {
        return table;
    }

    public Object getId() {
        return id;
    }

    public RelationTableInfo getRelationTableInfo() {
        return relationTableInfo;
    }

    public boolean isRelationTable() {
        return relationTableInfo != null;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableIdPair that = (TableIdPair) o;

        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        return id != null ? idEqual(id, that.id) : that.id == null;

    }

    private boolean idEqual(Object id, Object thatId) {
        if (id instanceof Number && thatId instanceof Number) {
            return new Long(((Number) id).longValue()).equals(((Number) thatId).longValue());
        } else {
            return id.equals(thatId);
        }
    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TableIdPair{" +
            "table='" + table + '\'' +
            ", id=" + id +
            '}';
    }
}
