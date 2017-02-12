package elasta.orm.nm.query.builder;

import elasta.orm.nm.criteria.Func;

import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
public interface WhereBuilder {

    WhereBuilder add(Func conditionFunc);

    WhereBuilder add(List<Func> conditionFuncs);

    List<Func> build();
}
