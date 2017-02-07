package elasta.orm.nm.delete.impl;

import elasta.orm.nm.delete.DeleteContext;
import elasta.orm.nm.delete.DeleteDataPopulator;
import elasta.orm.nm.delete.DeleteFunction;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by Jango on 2017-01-25.
 */
public class DeleteFunctionImpl implements DeleteFunction {
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

        final DeleteData deleteData = deleteDataPopulator.populate(entity);

        context.add(deleteData);

        for (DirectDeleteDependency directDeleteDependency : directDeleteDependencies) {

            new JsonDependencyHandler(jsonObject -> {
                directDeleteDependency.getDeleteHandler().delete(jsonObject);
            }).handle(entity.getValue(directDeleteDependency.getFieldName()));
        }

        for (IndirectDeleteDependency indirectDeleteDependency : indirectDeleteDependencies) {

            new JsonDependencyHandler(jsonObject -> {
                indirectDeleteDependency.getDeleteHandler().delete(jsonObject);
            }).handle(entity.getValue(indirectDeleteDependency.getFieldName()));
        }

        for (BelongsToDeleteDependency belongsToDeleteDependency : belongsToDeleteDependencies) {
            new JsonDependencyHandler(jsonObject -> {
                belongsToDeleteDependency.getDeleteHandler().delete(jsonObject);
            }).handle(entity.getValue(belongsToDeleteDependency.getFieldName()));
        }

        return deleteData;
    }
}
