package elasta.orm.delete.builder;

import elasta.orm.delete.DeleteFunction;
import elasta.orm.delete.builder.impl.DeleteFunctionBuilderImpl;

import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
public interface DeleteFunctionBuilder {
    DeleteFunction build(
        Params params
    );

    final class Params {
        final String entity;
        final DeleteTableFunctionBuilderContext deleteTableFunctionBuilderContext;
        final ListTablesToDeleteFunctionBuilderContext listTablesToDeleteFunctionBuilderContext;

        public Params(String entity, DeleteTableFunctionBuilderContext deleteTableFunctionBuilderContext, ListTablesToDeleteFunctionBuilderContext listTablesToDeleteFunctionBuilderContext) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(deleteTableFunctionBuilderContext);
            Objects.requireNonNull(listTablesToDeleteFunctionBuilderContext);
            this.entity = entity;
            this.deleteTableFunctionBuilderContext = deleteTableFunctionBuilderContext;
            this.listTablesToDeleteFunctionBuilderContext = listTablesToDeleteFunctionBuilderContext;
        }

        public String getEntity() {
            return entity;
        }

        public DeleteTableFunctionBuilderContext getDeleteTableFunctionBuilderContext() {
            return deleteTableFunctionBuilderContext;
        }

        public ListTablesToDeleteFunctionBuilderContext getListTablesToDeleteFunctionBuilderContext() {
            return listTablesToDeleteFunctionBuilderContext;
        }
    }

    final class ParamsBuilder {
        private String entity;
        private DeleteTableFunctionBuilderContext deleteTableFunctionBuilderContext;
        private ListTablesToDeleteFunctionBuilderContext listTablesToDeleteFunctionBuilderContext;

        public ParamsBuilder setEntity(String entity) {
            this.entity = entity;
            return this;
        }

        public ParamsBuilder setDeleteTableFunctionBuilderContext(DeleteTableFunctionBuilderContext deleteTableFunctionBuilderContext) {
            this.deleteTableFunctionBuilderContext = deleteTableFunctionBuilderContext;
            return this;
        }

        public ParamsBuilder setListTablesToDeleteFunctionBuilderContext(ListTablesToDeleteFunctionBuilderContext listTablesToDeleteFunctionBuilderContext) {
            this.listTablesToDeleteFunctionBuilderContext = listTablesToDeleteFunctionBuilderContext;
            return this;
        }

        public DeleteFunctionBuilderImpl.Params createParams() {
            return new DeleteFunctionBuilderImpl.Params(entity, deleteTableFunctionBuilderContext, listTablesToDeleteFunctionBuilderContext);
        }
    }
}
