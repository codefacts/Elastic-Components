package elasta.orm.nm.query.builder;

import elasta.orm.nm.criteria.Func;

import java.util.List;

/**
 * Created by Jango on 17/02/09.
 */
public interface SelectBuilder {

    SelectBuilder add(Func selection);

    SelectBuilder add(List<Func> selections);

    List<Func> build();
}
