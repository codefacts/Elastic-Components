package elasta.orm.query.builder;

import elasta.criteria.Func;import elasta.criteria.Func;

import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
public interface WhereBuilder {

    WhereBuilder add(Func conditionFunc);

    WhereBuilder add(List<Func> conditionFuncs);

    List<Func> build();
}
