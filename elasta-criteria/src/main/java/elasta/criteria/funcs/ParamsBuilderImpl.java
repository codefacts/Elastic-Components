package elasta.criteria.funcs;

import com.google.common.collect.ImmutableList;
import elasta.criteria.ParamsBuilder;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-07.
 */
public class ParamsBuilderImpl implements ParamsBuilder {
    final ImmutableList.Builder<Object> listBuilder;

    public ParamsBuilderImpl(ImmutableList.Builder<Object> listBuilder) {
        Objects.requireNonNull(listBuilder);
        this.listBuilder = listBuilder;
    }

    @Override
    public String add(Object value) {
        listBuilder.add(value);
        return "?";
    }
}
