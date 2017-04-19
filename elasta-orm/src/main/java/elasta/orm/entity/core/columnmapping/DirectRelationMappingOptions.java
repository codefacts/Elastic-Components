package elasta.orm.entity.core.columnmapping;

import elasta.orm.entity.core.columnmapping.RelationMappingOptions;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 4/18/2017.
 */
public interface DirectRelationMappingOptions extends RelationMappingOptions {

    @Override
    public CascadeUpsert getCascadeUpsert();

    @Override
    CascadeDelete getCascadeDelete();

    LoadAndDeleteParent getLoadAndDeleteParent();

    enum LoadAndDeleteParent {
        LOAD_AND_DELETE, SET_TO_NULL, NO_OPERATION
    }
}
