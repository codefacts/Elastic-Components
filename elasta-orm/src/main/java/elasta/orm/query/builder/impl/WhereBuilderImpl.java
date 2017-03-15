package elasta.orm.query.builder.impl;

import com.google.common.collect.ImmutableList;
import elasta.criteria.Func;
import elasta.orm.query.builder.WhereBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
final public class WhereBuilderImpl implements WhereBuilder {
    final List<Func> funcList = new ArrayList<>();

    @Override
    public WhereBuilder add(Func conditionFunc) {
        funcList.add(conditionFunc);
        return this;
    }

    @Override
    public WhereBuilder add(List<Func> conditionFuncs) {
        funcList.addAll(conditionFuncs);
        return this;
    }

    @Override
    public List<Func> build() {
        return ImmutableList.copyOf(funcList);
    }
}
