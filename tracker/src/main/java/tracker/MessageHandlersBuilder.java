package tracker;

import elasta.composer.message.handlers.MessageHandler;
import elasta.module.ModuleSystem;
import elasta.orm.query.expression.FieldExpression;
import lombok.Builder;
import lombok.Value;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
public interface MessageHandlersBuilder {

    List<AddressAndHandler> build(BuildParams params);

    @Value
    @Builder
    final class AddressAndHandler {
        final String address;
        final MessageHandler messageHandler;

        public AddressAndHandler(String address, MessageHandler messageHandler) {
            Objects.requireNonNull(address);
            Objects.requireNonNull(messageHandler);
            this.address = address;
            this.messageHandler = messageHandler;
        }
    }

    @Value
    @Builder
    final class BuildParams {
        final ModuleSystem module;
        final Collection<FlowParams> flowParamss;

        public BuildParams(ModuleSystem module, Collection<FlowParams> flowParamss) {
            Objects.requireNonNull(module);
            Objects.requireNonNull(flowParamss);
            this.module = module;
            this.flowParamss = flowParamss;
        }
    }

    @Value
    @Builder
    final class FlowParams {
        final String entity;
        final FieldExpression paginationKey;

        public FlowParams(String entity, FieldExpression paginationKey) {
            Objects.requireNonNull(entity);
            Objects.requireNonNull(paginationKey);
            this.entity = entity;
            this.paginationKey = paginationKey;
        }
    }
}
