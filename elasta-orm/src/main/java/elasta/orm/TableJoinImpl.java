package elasta.orm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by Jango on 9/15/2016.
 */
final public class TableJoinImpl implements TableJoin {
    private RootTuple root;
    private Collection<JoinTuple> joinTuples;

    public TableJoinImpl(RootTuple root, Collection<JoinTuple> joinTuples) {
        this.root = root;
        this.joinTuples = joinTuples;
    }

    @Override
    public String toSqlString() {
        return appendTo(new StringBuilder()).toString();
    }

    @Override
    public StringBuilder appendTo(StringBuilder stringBuilder) {

        for (int i = 0; i < joinTuples.size(); i++) {
            stringBuilder.append("(");
        }

        stringBuilder.append(root.getRoot() + " " + root.getAlias());

        String prevAlias = root.getAlias();
        for (JoinTuple joinTuple : joinTuples) {
            stringBuilder.append(" ").append(joinTuple.getJoinType()).append(" ")
                .append(joinTuple.getJoinTable()).append(" ").append(joinTuple.getJoinTableAlias())
                .append(" on ").append(prevAlias).append(".").append(joinTuple.getColumn())
                .append(" = ").append(joinTuple.getJoinTableAlias()).append(".").append(joinTuple.getJoinColumn())
                .append(")");
            prevAlias = joinTuple.getJoinTableAlias();
        }

        return stringBuilder;
    }

    public static void main(String[] args) {


    }
}
