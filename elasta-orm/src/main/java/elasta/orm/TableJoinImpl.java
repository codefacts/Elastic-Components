package elasta.orm;

import elasta.commons.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static elasta.commons.Utils.not;

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

        if (root.getRoot().equalsIgnoreCase(root.getAlias())) {

            stringBuilder.append(root.getRoot());
        } else {

            stringBuilder.append(root.getRoot() + " " + root.getAlias());
        }

        String prevAlias = root.getAlias();
        for (JoinTuple joinTuple : joinTuples) {

            String joinTableAlias = joinTuple.getJoinTableAlias();
            String joinTable = joinTuple.getJoinTable();

            stringBuilder.append(" ").append(joinTuple.getJoinType()).append(" ");

            if (joinTableAlias.equalsIgnoreCase(joinTable)) {

                stringBuilder.append(joinTable);
            } else {

                stringBuilder.append(joinTable).append(" ").append(joinTableAlias);
            }

            stringBuilder
                .append(" on ").append(prevAlias).append(".").append(joinTuple.getColumn())
                .append(" = ").append(joinTableAlias).append(".").append(joinTuple.getJoinColumn())
                .append(")");
            prevAlias = joinTableAlias;
        }

        return stringBuilder;
    }

    public static void main(String[] args) {


    }
}
