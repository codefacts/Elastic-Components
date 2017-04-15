package elasta.orm.event.builder.impl;

import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.EntityUtils;
import elasta.orm.entity.core.JavaType;
import elasta.orm.event.builder.ChildField;
import elasta.orm.event.builder.ListChildsForEventHandlerFunction;
import elasta.orm.upsert.UpsertUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/30/2017.
 */
final public class ListChildsForUpsertEventHandlerFunctionImpl implements ListChildsForEventHandlerFunction {
    final EntityMappingHelper helper;

    public ListChildsForUpsertEventHandlerFunctionImpl(EntityMappingHelper helper) {
        Objects.requireNonNull(helper);
        this.helper = helper;
    }

    @Override
    public List<ChildField> listChildFields(String entity) {
        return UpsertUtils.getRelationMappingsForUpsert(helper.getDbMapping(entity))
            .map(relationMapping -> new ChildField(
                relationMapping.getField(),
                relationMapping.getReferencingEntity(),
                helper.getField(entity, relationMapping.getField()).getJavaType() == JavaType.ARRAY ? ChildField.FieldType.Array : ChildField.FieldType.Object
            ))
            .collect(Collectors.toList())
            ;
    }
}
