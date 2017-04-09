package elasta.orm.relation.delete.impl;

import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 4/8/2017.
 */
@Value
final public class SrcFieldToRelationTableColumnMapping {
    final String srcField;
    final String dstColumn;

    public SrcFieldToRelationTableColumnMapping(String srcField, String dstColumn) {
        Objects.requireNonNull(srcField);
        Objects.requireNonNull(dstColumn);
        this.srcField = srcField;
        this.dstColumn = dstColumn;
    }
}
