package elasta.orm.jpa;

import java.util.Set;

/**
 * Created by Jango on 10/8/2016.
 */
public class Pairs {
    private final Set<TableIdPair> tableIdPairs;
    private final Set<RelationTableIdPair> relationTableIdPairs;

    public Pairs(Set<TableIdPair> tableIdPairs, Set<RelationTableIdPair> relationTableIdPairs) {
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

        Pairs pairs = (Pairs) o;

        if (tableIdPairs != null ? !tableIdPairs.equals(pairs.tableIdPairs) : pairs.tableIdPairs != null)
            return false;
        return relationTableIdPairs != null ? relationTableIdPairs.equals(pairs.relationTableIdPairs) : pairs.relationTableIdPairs == null;

    }

    @Override
    public int hashCode() {
        int result = tableIdPairs != null ? tableIdPairs.hashCode() : 0;
        result = 31 * result + (relationTableIdPairs != null ? relationTableIdPairs.hashCode() : 0);
        return result;
    }
}
