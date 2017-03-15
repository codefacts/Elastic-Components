package elasta.orm.entitymodel.columnmapping;

import elasta.orm.entitymodel.ForeignColumnMapping;

import java.util.List;

/**
 * Created by Jango on 2017-01-12.
 */
public interface DirectDbColumnMapping extends DbColumnMapping {

    String getReferencingTable();

    String getReferencingEntity();

    List<ForeignColumnMapping> getForeignColumnMappingList();
}
