package elasta.orm.delete.builder;

import elasta.orm.delete.ListTablesToDeleteFunction;

/**
 * Created by sohan on 3/12/2017.
 */
public interface ListTablesToDeleteFunctionBuilder {
    ListTablesToDeleteFunction build(String entity, ListTablesToDeleteFunctionBuilderContext context);
}
