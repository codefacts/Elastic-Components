package elasta.orm.nm.entitymodel.columnmapping;

import elasta.orm.nm.entitymodel.ForeignColumnMapping;

import java.util.List;

/**
 * Created by Jango on 2017-01-12.
 */
public interface IndirectColumnMapping extends DbColumnMapping {

    String getReferencingTable();

    String getRelationTable();

    List<ForeignColumnMapping> getSrcForeignColumnMappingList();

    List<ForeignColumnMapping> getDstForeignColumnMappingList();

}
