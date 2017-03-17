package elasta.orm.entity.core.columnmapping;

/**
 * Created by sohan on 3/17/2017.
 */
public interface RelationMapping extends DbColumnMapping {

    String getReferencingTable();

    String getReferencingEntity();
}
