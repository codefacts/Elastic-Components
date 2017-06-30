package tracker.impl;

import com.google.common.collect.ImmutableList;
import elasta.authorization.Authorizer;
import elasta.composer.ConvertersMap;
import elasta.composer.MessageBus;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.impl.AddMessageHandlerBuilderImpl;
import elasta.composer.message.handlers.builder.impl.FindOneMessageHandlerBuilderImpl;
import elasta.composer.model.response.builder.AuthorizationErrorModelBuilder;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.state.handlers.response.generator.JsonObjectResponseGenerator;
import elasta.core.promise.impl.Promises;
import elasta.module.ModuleSystem;
import elasta.orm.Orm;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import elasta.sql.SqlDB;
import tracker.Addresses;
import tracker.AppHelpers;
import tracker.AppUtils;
import tracker.MessageHandlersBuilder;
import tracker.entity_config.Entities;
import tracker.model.UserModel;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/30/2017.
 */
final public class MessageHandlersBuilderImpl implements MessageHandlersBuilder {
    @Override
    public List<AddressAndHandler> build(ModuleSystem module) {

        return new IIBuilder(module).build();
    }


    private final class IIBuilder {
        final ModuleSystem module;
        final ImmutableList.Builder<AddressAndHandler> addressAndHandlerBuilder = ImmutableList.builder();

        public IIBuilder(ModuleSystem module) {
            this.module = module;
        }

        public List<AddressAndHandler> build() {

            consumer(
                Addresses.userCreate,
                new AddMessageHandlerBuilderImpl(
                    module.require(Authorizer.class),
                    module.require(ConvertersMap.class),
                    Addresses.userCreate,
                    module.require(AuthorizationErrorModelBuilder.class),
                    Entities.USER,
                    UserModel.id,
                    asyncUserValidator(),
                    module.require(ValidationErrorModelBuilder.class),
                    module.require(Orm.class),
                    module.require(MessageBus.class),
                    Addresses.post(Addresses.userCreate),
                    module.require(JsonObjectResponseGenerator.class),
                    module.require(FlowToJsonObjectMessageHandlerConverter.class),
                    module.require(ObjectIdGenerator.class),
                    module.require(SqlDB.class)
                ).build()
            );

            {
                final String rootAlias = "r";

                consumer(
                    Addresses.userFindOne,
                    new FindOneMessageHandlerBuilderImpl<Long>(
                        rootAlias,
                        Entities.USER,
                        fieldSelections(Entities.USER, rootAlias),
                        module.require(Orm.class),
                        module.require(Authorizer.class),
                        Addresses.userFindOne,
                        module.require(AuthorizationErrorModelBuilder.class),
                        module.require(ConvertersMap.class),
                        module.require(FlowToJsonObjectMessageHandlerConverter.class)
                    ).build()
                );

            }

            return addressAndHandlerBuilder.build();
        }

        private List<FieldExpression> fieldSelections(String entity, String rootAlias) {
            return ImmutableList.copyOf(
                module.require(AppHelpers.class).findOneFields(entity)
                    .stream()
                    .map(field -> new FieldExpressionImpl(rootAlias + "." + field))
                    .collect(Collectors.toList())
            );
        }

        private void consumer(String address, MessageHandler messageHandler) {
            addressAndHandlerBuilder.add(
                new AddressAndHandler(
                    address, messageHandler
                )
            );
        }

        private JsonObjectValidatorAsync asyncUserValidator() {
            return val -> Promises.of(ImmutableList.of());
        }
    }
}
