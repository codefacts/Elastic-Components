package elasta.orm.entity.core.columnmapping;

/**
 * Created by sohan on 4/18/2017.
 */
public interface RelationMappingOptions {

    CascadeUpsert getCascadeUpsert();

    CascadeDelete getCascadeDelete();

    boolean isMandatory();

    enum CascadeUpsert {
        YES, NO
    }

    enum CascadeDelete {
        YES, NO
    }
}
