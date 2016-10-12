package elasta.orm.jpa;

import java.util.Set;

/**
 * Created by Jango on 10/8/2016.
 */
public class TableIdPairs {
    private final Set<TableIdPair> tableIdPairs;
    private final Set<RelationTableIdPair> relationTableIdPairs;

    public TableIdPairs(Set<TableIdPair> tableIdPairs, Set<RelationTableIdPair> relationTableIdPairs) {
        this.tableIdPairs = tableIdPairs;
        this.relationTableIdPairs = relationTableIdPairs;
    }

    public Set<TableIdPair> getTableIdPairs() {
        return tableIdPairs;
    }

    public Set<RelationTableIdPair> getRelationTableIdPairs() {
        return relationTableIdPairs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TableIdPairs tableIdPairs = (TableIdPairs) o;

        if (this.tableIdPairs != null ? !this.tableIdPairs.equals(tableIdPairs.tableIdPairs) : tableIdPairs.tableIdPairs != null)
            return false;
        return relationTableIdPairs != null ? relationTableIdPairs.equals(tableIdPairs.relationTableIdPairs) : tableIdPairs.relationTableIdPairs == null;

    }

    @Override
    public int hashCode() {
        int result = tableIdPairs != null ? tableIdPairs.hashCode() : 0;
        result = 31 * result + (relationTableIdPairs != null ? relationTableIdPairs.hashCode() : 0);
        return result;
    }
}
