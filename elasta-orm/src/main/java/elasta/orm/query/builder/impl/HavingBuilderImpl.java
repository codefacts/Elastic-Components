package elasta.orm.query.builder.impl;

import elasta.criteria.Func;
import elasta.orm.query.builder.HavingBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
final public class HavingBuilderImpl implements HavingBuilder {
    final List<Func> funcList = new ArrayList<>();

    @Override
    public HavingBuilder add(Func func) {
        funcList.add(func);
        return this;
    }

    @Override
    public HavingBuilder add(List<Func> funcs) {
        funcList.addAll(funcs);
        return this;
    }

    @Override
    public List<Func> build() {
        return funcList;
    }

    public List<Func> getFuncList() {
        return funcList;
    }
}
