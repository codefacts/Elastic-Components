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
    final LoadAndDelete loadAndDelete;

    public DirectRelationMappingOptionsImpl(CascadeUpsert cascadeUpsert, CascadeDelete cascadeDelete, boolean mandatory, LoadAndDelete loadAndDelete) {
        Objects.requireNonNull(cascadeUpsert);
        Objects.requireNonNull(cascadeDelete);
        Objects.requireNonNull(loadAndDelete);
        this.cascadeUpsert = cascadeUpsert;
        this.cascadeDelete = cascadeDelete;
        this.mandatory = mandatory;
        this.loadAndDelete = loadAndDelete;
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

    public LoadAndDelete getLoadAndDelete() {
        return loadAndDelete;
    }
}
