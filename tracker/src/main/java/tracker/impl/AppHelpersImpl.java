package tracker.impl;

import elasta.orm.entity.EntityMappingHelper;
import elasta.orm.entity.core.Field;
import tracker.AppHelpers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/30/2017.
 */
final public class AppHelpersImpl implements AppHelpers {
    final EntityMappingHelper helper;

    public AppHelpersImpl(EntityMappingHelper helper) {
        Objects.requireNonNull(helper);
        this.helper = helper;
    }

    @Override
    public List<String> findOneFields(String entity) {
        return Arrays.stream(helper.getFields(entity))
            .filter(field -> !field.getRelationship().isPresent())
            .map(Field::getName)
            .collect(Collectors.toList());
    }
}
