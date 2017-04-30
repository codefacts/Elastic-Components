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
    final boolean mandatory;

    public RelationMappingOptionsImpl(CascadeUpsert cascadeUpsert, CascadeDelete cascadeDelete, boolean mandatory) {
        Objects.requireNonNull(cascadeUpsert);
        Objects.requireNonNull(cascadeDelete);
        this.cascadeUpsert = cascadeUpsert;
        this.cascadeDelete = cascadeDelete;
        this.mandatory = mandatory;
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
}
