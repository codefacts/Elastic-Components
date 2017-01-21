package elasta.orm.nm.entitymodel.columnmapping;

import elasta.orm.nm.entitymodel.ForeignColumnMapping;

import java.util.List;

/**
 * Created by Jango on 2017-01-12.
 */
public interface VirtualColumnMapping extends DbColumnMapping {

    String getReferencingTable();

    String getReferencingEntity();

    List<ForeignColumnMapping> getForeignColumnMappingList();
}
