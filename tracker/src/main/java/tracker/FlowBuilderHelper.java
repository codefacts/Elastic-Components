package tracker;

import elasta.composer.flow.holder.*;
import elasta.module.ModuleSystem;
import elasta.orm.query.expression.FieldExpression;
import lombok.Builder;
import lombok.Value;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
public interface FlowBuilderHelper {

    AddFlowHolder addFlowHolder(AddParams params);

    AddAllFlowHolder addAllFlowHolder(AddAllParams params);

    UpdateFlowHolder updateFlowHolder(UpdateParams params);

    UpdateAllFlowHolder updateAllFlowHolder(UpdateAllParams params);

    DeleteFlowHolder deleteFlowHolder(DeleteParams params);

    DeleteAllFlowHolder deleteAllFlowHolder(DeleteAllParams params);

    FindOneFlowHolder findOneFlowHolder(FindOneParams params);

    FindAllFlowHolder findAllFlowHolder(FindAllParams params);

    @Value
    @Builder
    final class AddParams {
        final ModuleSystem module;
        final String action;
        final String entity;
        final String broadcastAddress;

        public AddParams(ModuleSystem module, String action, String entity, String broadcastAddress) {
            Objects.requireNonNull(module);
            Objects.requireNonNull(action);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(broadcastAddress);
            this.module = module;
            this.action = action;
            this.entity = entity;
            this.broadcastAddress = broadcastAddress;
        }
    }

    @Value
    @Builder
    final class AddAllParams {
        final ModuleSystem module;
        final String action;
        final String entity;
        final String broadcastAddress;

        public AddAllParams(ModuleSystem module, String action, String entity, String broadcastAddress) {
            Objects.requireNonNull(module);
            Objects.requireNonNull(action);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(broadcastAddress);
            this.module = module;
            this.action = action;
            this.entity = entity;
            this.broadcastAddress = broadcastAddress;
        }
    }

    @Value
    @Builder
    final class UpdateParams {
        final ModuleSystem module;
        final String action;
        final String entity;
        final String broadcastAddress;

        public UpdateParams(ModuleSystem module, String action, String entity, String broadcastAddress) {
            Objects.requireNonNull(module);
            Objects.requireNonNull(action);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(broadcastAddress);
            this.module = module;
            this.action = action;
            this.entity = entity;
            this.broadcastAddress = broadcastAddress;
        }

        public String getBroadcastAddress() {
            return broadcastAddress;
        }
    }

    @Value
    @Builder
    final class UpdateAllParams {
        final ModuleSystem module;
        final String action;
        final String entity;
        final String broadcastAddress;

        public UpdateAllParams(ModuleSystem module, String action, String entity, String broadcastAddress) {
            Objects.requireNonNull(module);
            Objects.requireNonNull(action);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(broadcastAddress);
            this.module = module;
            this.action = action;
            this.entity = entity;
            this.broadcastAddress = broadcastAddress;
        }

        public String getBroadcastAddress() {
            return broadcastAddress;
        }
    }

    @Value
    @Builder
    final class DeleteParams {
        final ModuleSystem module;
        final String action;
        final String entity;
        private String broadcastAddress;

        public DeleteParams(ModuleSystem module, String action, String entity, String broadcastAddress) {
            Objects.requireNonNull(module);
            Objects.requireNonNull(action);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(broadcastAddress);
            this.module = module;
            this.action = action;
            this.entity = entity;
            this.broadcastAddress = broadcastAddress;
        }

        public String getBroadcastAddress() {
            return broadcastAddress;
        }
    }

    @Value
    @Builder
    final class DeleteAllParams {
        final ModuleSystem module;
        final String action;
        final String entity;
        final String broadcastAddress;

        public DeleteAllParams(ModuleSystem module, String action, String entity, String broadcastAddress) {
            Objects.requireNonNull(module);
            Objects.requireNonNull(action);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(broadcastAddress);
            this.module = module;
            this.action = action;
            this.entity = entity;
            this.broadcastAddress = broadcastAddress;
        }

        public String getBroadcastAddress() {
            return broadcastAddress;
        }
    }

    @Value
    @Builder
    final class FindOneParams {
        final ModuleSystem module;
        final String action;
        final String entity;

        public FindOneParams(ModuleSystem module, String action, String entity) {
            Objects.requireNonNull(module);
            Objects.requireNonNull(action);
            Objects.requireNonNull(entity);
            this.module = module;
            this.action = action;
            this.entity = entity;
        }
    }

    @Value
    @Builder
    final class FindAllParams {
        final ModuleSystem module;
        final String action;
        final String entity;
        final FieldExpression paginationKey;

        public FindAllParams(ModuleSystem module, String action, String entity, FieldExpression paginationKey) {
            Objects.requireNonNull(module);
            Objects.requireNonNull(action);
            Objects.requireNonNull(entity);
            Objects.requireNonNull(paginationKey);
            this.module = module;
            this.action = action;
            this.entity = entity;
            this.paginationKey = paginationKey;
        }

        public FieldExpression getPaginationKey() {
            return paginationKey;
        }
    }
}
