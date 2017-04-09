package elasta.orm.relation.delete.impl;

import com.google.common.collect.ImmutableList;
import elasta.sql.core.ColumnValuePair;
import lombok.Builder;
import lombok.Value;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/8/2017.
 */
@Value
@Builder
final public class DeleteRelationData {
    private final OperationType operationType;
    private final String referencingTable;
    private final List<ColumnValuePair> primaryColumnValuePairs;
    private final List<String> referencingColumns;

    public DeleteRelationData(OperationType operationType, String referencingTable, List<ColumnValuePair> primaryColumnValuePairs) {
        this(operationType, referencingTable, primaryColumnValuePairs, ImmutableList.of());
    }

    public DeleteRelationData(OperationType operationType, String referencingTable, List<ColumnValuePair> primaryColumnValuePairs, List<String> referencingColumns) {
        Objects.requireNonNull(operationType);
        Objects.requireNonNull(referencingTable);
        Objects.requireNonNull(primaryColumnValuePairs);
        Objects.requireNonNull(referencingColumns);
        this.operationType = operationType;
        this.referencingTable = referencingTable;
        this.primaryColumnValuePairs = primaryColumnValuePairs;
        this.referencingColumns = referencingColumns;
    }

    public enum OperationType {
        DELETE, UPDATE
    }
}
