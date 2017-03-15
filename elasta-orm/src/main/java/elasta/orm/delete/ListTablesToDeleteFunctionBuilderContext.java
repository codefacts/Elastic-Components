package elasta.orm.delete;

/**
 * Created by sohan on 3/12/2017.
 */
public interface ListTablesToDeleteFunctionBuilderContext {
    boolean contains(String entity);

    ListTablesToDeleteFunction get(String entity);

    void putEmpty();

    void put(String entity, ListTablesToDeleteFunction listTablesToDeleteFunction);

    boolean isEmpty(String referencingEntity);
}
