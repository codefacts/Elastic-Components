package elasta.orm.nm.query.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.orm.nm.criteria.Func;
import elasta.orm.nm.query.builder.SelectBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Jango on 17/02/09.
 */
final public class SelectBuilderImpl implements SelectBuilder {
    final List<Func> funcList = new ArrayList<>();

    @Override
    public SelectBuilder add(Func selection) {
        funcList.add(selection);
        return this;
    }

    @Override
    public SelectBuilder add(List<Func> selections) {
        funcList.addAll(selections);
        return this;
    }

    @Override
    public List<Func> build() {
        return ImmutableList.copyOf(funcList);
    }

    public List<Func> getFuncList() {
        return funcList;
    }
}
