package elasta.orm.jpa;

/**
 * Created by Jango on 10/7/2016.
 */
public class TableIdPair {
    private final String table;
    private final Object id;

    public TableIdPair(String table, Object id) {
        this.table = table;

        id = id instanceof Number ? ((Number) id).longValue() : id;

        this.id = id;
    }

    public String getTable() {
        return table;
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableIdPair that = (TableIdPair) o;

        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;

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
