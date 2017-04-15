package elasta.orm.entity.core.columnmapping;

import elasta.orm.entity.core.ForeignColumnMapping;

import java.util.List;

/**
 * Created by Jango on 2017-01-12.
 */
public interface IndirectRelationMapping extends RelationMapping{

    String getReferencingTable();

    String getReferencingEntity();

    String getRelationTable();

    List<ForeignColumnMapping> getSrcForeignColumnMappingList();

    List<ForeignColumnMapping> getDstForeignColumnMappingList();

}
