package elasta.orm.delete.builder;

import elasta.orm.delete.DeleteFunction;
import elasta.orm.delete.DeleteTableFunction;
import elasta.orm.delete.ListTablesToDeleteFunction;
import elasta.orm.delete.builder.impl.DeleteFunctionBuilderImpl;
import elasta.orm.event.builder.BuilderContext;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 3/17/2017.
 */
public interface DeleteFunctionBuilder {
    DeleteFunction build(
        Params params
    );

    @Value
    @Builder
    final class Params {
        final String entity;
        final BuilderContext<DeleteTableFunction> deleteTableFunctionBuilderContext;
        final BuilderContext<ListTablesToDeleteFunction> listTablesToDeleteFunctionBuilderContext;

        public Params(String entity, BuilderContext<DeleteTableFunction> deleteTableFunctionBuilderContext, BuilderContext<ListTablesToDeleteFunction> listTablesToDeleteFunctionBuilderContext) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(deleteTableFunctionBuilderContext);
            Objects.requireNonNull(listTablesToDeleteFunctionBuilderContext);
            this.entity = entity;
            this.deleteTableFunctionBuilderContext = deleteTableFunctionBuilderContext;
            this.listTablesToDeleteFunctionBuilderContext = listTablesToDeleteFunctionBuilderContext;
        }
    }

    final class ParamsBuilder {
        private String entity;
        private BuilderContext<DeleteTableFunction> deleteTableFunctionBuilderContext;
        private BuilderContext<ListTablesToDeleteFunction> listTablesToDeleteFunctionBuilderContext;

        public ParamsBuilder setEntity(String entity) {
            this.entity = entity;
            return this;
        }

        public ParamsBuilder setDeleteTableFunctionBuilderContext(BuilderContext<DeleteTableFunction> deleteTableFunctionBuilderContext) {
            this.deleteTableFunctionBuilderContext = deleteTableFunctionBuilderContext;
            return this;
        }

        public ParamsBuilder setListTablesToDeleteFunctionBuilderContext(BuilderContext<ListTablesToDeleteFunction> listTablesToDeleteFunctionBuilderContext) {
            this.listTablesToDeleteFunctionBuilderContext = listTablesToDeleteFunctionBuilderContext;
            return this;
        }

        public DeleteFunctionBuilderImpl.Params createParams() {
            return new DeleteFunctionBuilderImpl.Params(entity, deleteTableFunctionBuilderContext, listTablesToDeleteFunctionBuilderContext);
        }
    }
}
