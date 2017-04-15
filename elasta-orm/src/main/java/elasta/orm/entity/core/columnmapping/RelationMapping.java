package elasta.orm.entity.core.columnmapping;

import elasta.orm.entity.core.RelationType;

/**
 * Created by sohan on 3/17/2017.
 */
public interface RelationMapping {

    String getField();

    RelationType getColumnType();

    String getReferencingTable();

    String getReferencingEntity();
}
