package elasta.orm.delete.impl;

import elasta.commons.Utils;
import elasta.orm.delete.*;
import elasta.orm.delete.impl.JsonDependencyHandler;
import elasta.orm.upsert.TableDataPopulator;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 3/12/2017.
 */
final public class ListTablesToDeleteFunctionImpl implements ListTablesToDeleteFunction {
    final TableDataPopulator tableDataPopulator;
    final DirectChildHandler[] directChildHandlers;
    final IndirectChildHandler[] indirectChildHandlers;
    final VirtualChildHandler[] virtualChildHandlers;

    public ListTablesToDeleteFunctionImpl(TableDataPopulator tableDataPopulator, DirectChildHandler[] directChildHandlers, IndirectChildHandler[] indirectChildHandlers, VirtualChildHandler[] virtualChildHandlers) {
        Objects.requireNonNull(tableDataPopulator);
        Objects.requireNonNull(directChildHandlers);
        Objects.requireNonNull(indirectChildHandlers);
        Objects.requireNonNull(virtualChildHandlers);
        this.tableDataPopulator = tableDataPopulator;
        this.directChildHandlers = directChildHandlers;
        this.indirectChildHandlers = indirectChildHandlers;
        this.virtualChildHandlers = virtualChildHandlers;
    }

    @Override
    public void listTables(JsonObject entity, ListTablesToDeleteContext context) {

        context.add(
            tableDataPopulator.populate(entity)
        );

        for (DirectChildHandler directChildHandler : directChildHandlers) {
            final String field = directChildHandler.field();
            JsonObject jsonObject = entity.getJsonObject(field);
            if (jsonObject == null) {
                continue;
            }
            directChildHandler.handle(jsonObject, context);
        }

        for (IndirectChildHandler indirectChildHandler : indirectChildHandlers) {
            String field = indirectChildHandler.field();

            Object value = entity.getValue(
                field
            );

            if (value == null) {
                continue;
            }

            new JsonDependencyHandler(jsonObject -> indirectChildHandler.handle(entity, jsonObject, context))
                .handle(
                    value
                );
        }

        for (VirtualChildHandler virtualChildHandler : virtualChildHandlers) {
            final String field = virtualChildHandler.field();

            Object value = entity.getValue(
                field
            );

            if (value == null) {
                continue;
            }

            new JsonDependencyHandler(jsonObject -> virtualChildHandler.handle(entity, jsonObject, context))
                .handle(
                    value
                );
        }
    }
}
