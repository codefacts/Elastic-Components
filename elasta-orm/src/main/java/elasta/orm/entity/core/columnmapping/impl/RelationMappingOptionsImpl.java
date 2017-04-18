package elasta.orm.entity.core.columnmapping.impl;

import elasta.orm.entity.core.columnmapping.RelationMappingOptions;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 4/18/2017.
 */
@Value
final public class RelationMappingOptionsImpl implements RelationMappingOptions {
    final CascadeUpsert cascadeUpsert;
    final CascadeDelete cascadeDelete;

    public RelationMappingOptionsImpl(CascadeUpsert cascadeUpsert, CascadeDelete cascadeDelete) {
        Objects.requireNonNull(cascadeUpsert);
        Objects.requireNonNull(cascadeDelete);
        this.cascadeUpsert = cascadeUpsert;
        this.cascadeDelete = cascadeDelete;
    }

    @Override
    public CascadeUpsert getCascadeUpsert() {
        return cascadeUpsert;
    }

    @Override
    public CascadeDelete getCascadeDelete() {
        return cascadeDelete;
    }
}
