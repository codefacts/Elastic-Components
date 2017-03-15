package elasta.orm.query.builder;

import elasta.criteria.Func;import elasta.criteria.Func;

import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
public interface HavingBuilder {

    HavingBuilder add(Func func);

    HavingBuilder add(List<Func> funcs);

    List<Func> build();
    
}
