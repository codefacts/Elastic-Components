package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.*;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-25.
 */
final public class DeleteFunctionImpl implements DeleteFunction {
    final DeleteDataPopulator deleteDataPopulator;
    final DirectDeleteDependency[] directDeleteDependencies;
    final IndirectDeleteDependency[] indirectDeleteDependencies;
    final BelongsToDeleteDependency[] belongsToDeleteDependencies;

    public DeleteFunctionImpl(DeleteDataPopulator deleteDataPopulator, DirectDeleteDependency[] directDeleteDependencies, IndirectDeleteDependency[] indirectDeleteDependencies, BelongsToDeleteDependency[] belongsToDeleteDependencies) {
        Objects.requireNonNull(deleteDataPopulator);
        Objects.requireNonNull(directDeleteDependencies);
        Objects.requireNonNull(indirectDeleteDependencies);
        Objects.requireNonNull(belongsToDeleteDependencies);
        this.deleteDataPopulator = deleteDataPopulator;
        this.directDeleteDependencies = directDeleteDependencies;
        this.indirectDeleteDependencies = indirectDeleteDependencies;
        this.belongsToDeleteDependencies = belongsToDeleteDependencies;
    }

    @Override
    public DeleteData delete(JsonObject entity, DeleteContext context) {

        for (IndirectDeleteDependency indirectDeleteDependency : indirectDeleteDependencies) {

            new JsonDependencyHandler(jsonObject -> {
                indirectDeleteDependency.getDeleteHandler().delete(entity, jsonObject, context);
            }).handle(entity.getValue(indirectDeleteDependency.getField()));
        }

        for (BelongsToDeleteDependency belongsToDeleteDependency : belongsToDeleteDependencies) {
            new JsonDependencyHandler(jsonObject -> {
                belongsToDeleteDependency.getDeleteHandler().delete(entity, jsonObject, context);
            }).handle(entity.getValue(belongsToDeleteDependency.getField()));
        }

        final DeleteData deleteData = deleteDataPopulator.populate(entity);

        context.add(deleteData);

        for (DirectDeleteDependency directDeleteDependency : directDeleteDependencies) {

            new JsonDependencyHandler(jsonObject -> {
                directDeleteDependency.getDeleteHandler().delete(jsonObject, context);
            }).handle(entity.getValue(directDeleteDependency.getField()));
        }

        return deleteData;
    }
}
