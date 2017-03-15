package elasta.orm.delete;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 3/12/2017.
 */
final public class DirectChildHandlerImpl implements DirectChildHandler {
    final String field;
    final ListTablesToDeleteFunction listTablesToDeleteFunction;

    public DirectChildHandlerImpl(String field, ListTablesToDeleteFunction listTablesToDeleteFunction) {
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
    public void handle(JsonObject entity, ListTablesToDeleteContext context) {
        listTablesToDeleteFunction.listTables(entity, context);
    }
}
