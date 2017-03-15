package elasta.orm.delete.dependency;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 3/12/2017.
 */
final public class IndirectChildHandlerImpl implements IndirectChildHandler {
    final String field;
    final ListTablesToDeleteFunction listTablesToDeleteFunction;

    public IndirectChildHandlerImpl(String field, ListTablesToDeleteFunction listTablesToDeleteFunction) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(listTablesToDeleteFunction);
        this.field = field;
        this.listTablesToDeleteFunction = listTablesToDeleteFunction;
    }

    @Override
    public String field() {
        return field;
    }

    @Override
    public void handle(JsonObject parent, JsonObject child, ListTablesToDeleteContext context) {
        listTablesToDeleteFunction.listTables(child, context);
    }
}
