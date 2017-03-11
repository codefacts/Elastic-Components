package elasta.orm.nm.delete.dependency;

import elasta.commons.Utils;
import elasta.orm.nm.delete.impl.JsonDependencyHandler;
import elasta.orm.nm.upsert.TableDataPopulator;
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
            if (Utils.not(entity.containsKey(field))) {
                continue;
            }
            directChildHandler.handle(entity.getJsonObject(field), context);
        }

        for (IndirectChildHandler indirectChildHandler : indirectChildHandlers) {
            String field = indirectChildHandler.field();
            if (Utils.not(entity.containsKey(field))) {
                continue;
            }

            new JsonDependencyHandler(jsonObject -> indirectChildHandler.handle(entity, jsonObject, context))
                .handle(
                    entity.getValue(
                        field
                    )
                );
        }

        for (VirtualChildHandler virtualChildHandler : virtualChildHandlers) {
            final String field = virtualChildHandler.field();
            if (Utils.not(entity.containsKey(field))) {
                continue;
            }

            new JsonDependencyHandler(jsonObject -> virtualChildHandler.handle(entity, jsonObject, context))
                .handle(
                    entity.getValue(
                        field
                    )
                );
        }
    }
}
