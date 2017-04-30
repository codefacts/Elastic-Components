package elasta.orm.entity.core.columnmapping.impl;

import elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions;

import java.util.Objects;

/**
 * Created by sohan on 4/18/2017.
 */
final public class DirectRelationMappingOptionsImpl implements DirectRelationMappingOptions {
    final CascadeUpsert cascadeUpsert;
    final CascadeDelete cascadeDelete;
    final boolean mandatory;
    final elasta.orm.entity.core.columnmapping.DirectRelationMappingOptions.LoadAndDeleteParent loadAndDeleteParent;

    public DirectRelationMappingOptionsImpl(CascadeUpsert cascadeUpsert, CascadeDelete cascadeDelete, boolean mandatory, LoadAndDeleteParent loadAndDeleteParent) {
        Objects.requireNonNull(cascadeUpsert);
        Objects.requireNonNull(cascadeDelete);
        Objects.requireNonNull(loadAndDeleteParent);
        this.cascadeUpsert = cascadeUpsert;
        this.cascadeDelete = cascadeDelete;
        this.mandatory = mandatory;
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
    public boolean isMandatory() {
        return mandatory;
    }

    @Override
    public DirectRelationMappingOptions.LoadAndDeleteParent getLoadAndDeleteParent() {
        return loadAndDeleteParent;
    }
}
