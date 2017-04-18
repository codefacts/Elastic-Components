package elasta.orm.entity.core.columnmapping.impl;

import elasta.orm.entity.core.columnmapping.DirectRelationMapping;
import elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions;
import elasta.orm.entity.core.columnmapping.RelationMappingOptions;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 4/18/2017.
 */
final public class DirectRelationMappingOptionsImpl implements DirectRelationMappingOptions {
    final CascadeUpsert cascadeUpsert;
    final CascadeDelete cascadeDelete;
    final elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions.LoadAndDeleteParent loadAndDeleteParent;

    public DirectRelationMappingOptionsImpl(CascadeUpsert cascadeUpsert, CascadeDelete cascadeDelete, elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions.LoadAndDeleteParent loadAndDeleteParent) {
        Objects.requireNonNull(cascadeUpsert);
        Objects.requireNonNull(cascadeDelete);
        Objects.requireNonNull(loadAndDeleteParent);
        this.cascadeUpsert = cascadeUpsert;
        this.cascadeDelete = cascadeDelete;
        this.loadAndDeleteParent = loadAndDeleteParent;
    }

    @Override
    public CascadeUpsert getCascadeUpsert() {
        return cascadeUpsert;
    }

    @Override
    public CascadeDelete getCascadeDelete() {
        return cascadeDelete;
    }

    @Override
    public DirectRelationMappingOptions.LoadAndDeleteParent getLoadAndDeleteParent() {
        return loadAndDeleteParent;
    }
}
