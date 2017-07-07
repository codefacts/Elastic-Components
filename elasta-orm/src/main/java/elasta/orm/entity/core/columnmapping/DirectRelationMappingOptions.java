package elasta.orm.entity.core.columnmapping;

/**
 * Created by sohan on 4/18/2017.
 */
public interface DirectRelationMappingOptions extends RelationMappingOptions {

    @Override
    public CascadeUpsert getCascadeUpsert();

    @Override
    CascadeDelete getCascadeDelete();

    LoadAndDelete getLoadAndDelete();

    enum LoadAndDelete {
        LOAD_AND_DELETE, SET_TO_NULL, NO_OPERATION
    }
}
