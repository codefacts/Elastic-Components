package tracker.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.converter.FlowToJsonArrayMessageHandlerConverter;
import elasta.composer.converter.FlowToJsonObjectMessageHandlerConverter;
import elasta.composer.converter.FlowToMessageHandlerConverter;
import elasta.composer.flow.holder.*;
import elasta.composer.message.handlers.MessageHandler;
import elasta.composer.message.handlers.builder.impl.*;
import elasta.module.ModuleSystem;
import tracker.*;
import tracker.entity_config.Entities;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class MessageHandlersBuilderImpl implements MessageHandlersBuilder {
    final FlowBuilderHelper flowBuilderHelper;

    public MessageHandlersBuilderImpl(FlowBuilderHelper flowBuilderHelper) {
        Objects.requireNonNull(flowBuilderHelper);
        this.flowBuilderHelper = flowBuilderHelper;
    }

    @Override
    public List<AddressAndHandler> build(BuildParams params) {

        Objects.requireNonNull(params);

        return new IIBuilder(params.getModule()).build(params.getFlowParamss());
    }

    private final class IIBuilder {
        final ModuleSystem module;
        final ImmutableList.Builder<AddressAndHandler> addressAndHandlerBuilder = ImmutableList.builder();

        public IIBuilder(ModuleSystem module) {
            this.module = module;
        }

        public List<AddressAndHandler> build(Collection<FlowParams> flowParamss) {

            flowParamss.forEach(flowParams -> {

                addressAndHandlerBuilder.add(
                    addMessageHandler(flowParams)
                );

                addressAndHandlerBuilder.add(
                    addAllMessageHandler(flowParams)
                );

                addressAndHandlerBuilder.add(
                    updateMessageHandler(flowParams)
                );

                addressAndHandlerBuilder.add(
                    updateAllMessageHandler(flowParams)
                );

                addressAndHandlerBuilder.add(
                    deleteMessageHandler(flowParams)
                );

                addressAndHandlerBuilder.add(
                    deleteAllMessageHandler(flowParams)
                );

                addressAndHandlerBuilder.add(
                    findOneMessageHandler(flowParams)
                );

                addressAndHandlerBuilder.add(
                    findAllMessageHandler(flowParams)
                );

            });

            return addressAndHandlerBuilder.build();
        }

        private AddressAndHandler findAllMessageHandler(FlowParams flowParams) {
            String findAll = Addresses.findAll(flowParams.getEntity());
            return consumer(
                findAll,
                new FindAllMessageHandlerBuilderImpl(
                    flowBuilderHelper.findAllFlowHolder(
                        FlowBuilderHelper.FindAllParams.builder()
                            .module(module)
                            .entity(flowParams.getEntity())
                            .action(findAll)
                            .paginationKey(flowParams.getPaginationKey())
                            .build()
                    ),
                    module.require(FlowToJsonObjectMessageHandlerConverter.class)
                ).build()
            );
        }

        private AddressAndHandler findOneMessageHandler(FlowParams flowParams) {
            String findOne = Addresses.findOne(flowParams.getEntity());
            return consumer(
                findOne,
                new FindOneMessageHandlerBuilderImpl<>(
                    flowBuilderHelper.findOneFlowHolder(
                        FlowBuilderHelper.FindOneParams.builder()
                            .module(module)
                            .entity(flowParams.getEntity())
                            .action(findOne)
                            .build()
                    ),
                    module.require(FlowToJsonObjectMessageHandlerConverter.class)
                ).build()
            );
        }

        private AddressAndHandler deleteAllMessageHandler(FlowParams flowParams) {
            String deleteAll = Addresses.deleteAll(flowParams.getEntity());
            return consumer(
                deleteAll,
                new DeleteAllMessageHandlerBuilderImpl(
                    flowBuilderHelper.deleteAllFlowHolder(
                        FlowBuilderHelper.DeleteAllParams.builder()
                            .module(module)
                            .entity(flowParams.getEntity())
                            .action(deleteAll)
                            .broadcastAddress(
                                Addresses.post(
                                    Addresses.delete(
                                        flowParams.getEntity()
                                    )
                                )
                            )
                            .build()
                    ),
                    module.require(FlowToJsonArrayMessageHandlerConverter.class)
                ).build()
            );
        }

        private AddressAndHandler deleteMessageHandler(FlowParams flowParams) {
            String delete = Addresses.delete(flowParams.getEntity());
            return consumer(
                delete,
                new DeleteMessageHandlerBuilderImpl<>(
                    flowBuilderHelper.deleteFlowHolder(
                        FlowBuilderHelper.DeleteParams.builder()
                            .module(module)
                            .entity(flowParams.getEntity())
                            .action(delete)
                            .broadcastAddress(Addresses.post(delete))
                            .build()
                    ),
                    module.require(FlowToMessageHandlerConverter.class)
                ).build()
            );
        }

        private AddressAndHandler updateAllMessageHandler(FlowParams flowParams) {
            String updateAll = Addresses.updateAll(flowParams.getEntity());
            return consumer(
                updateAll,
                new UpdateAllMessageHandlerBuilderImpl(
                    flowBuilderHelper.updateAllFlowHolder(
                        FlowBuilderHelper.UpdateAllParams.builder()
                            .module(module)
                            .entity(flowParams.getEntity())
                            .action(updateAll)
                            .broadcastAddress(Addresses.post(Addresses.update(flowParams.getEntity())))
                            .build()
                    ),
                    module.require(FlowToJsonArrayMessageHandlerConverter.class)
                ).build()
            );
        }

        private AddressAndHandler updateMessageHandler(FlowParams flowParams) {
            String update = Addresses.update(flowParams.getEntity());
            return consumer(
                update,
                new UpdateMessageHandlerBuilderImpl(
                    flowBuilderHelper.updateFlowHolder(
                        FlowBuilderHelper.UpdateParams.builder()
                            .module(module)
                            .entity(flowParams.getEntity())
                            .action(update)
                            .broadcastAddress(Addresses.post(update))
                            .build()
                    ),
                    module.require(FlowToJsonObjectMessageHandlerConverter.class)
                ).build()
            );
        }

        private AddressAndHandler addAllMessageHandler(FlowParams flowParams) {

            final String addAllAddress = Addresses.addAll(flowParams.getEntity());

            return addAllHandler(
                addAllAddress,
                flowBuilderHelper.addAllFlowHolder(
                    FlowBuilderHelper.AddAllParams.builder()
                        .module(module)
                        .entity(flowParams.getEntity())
                        .action(addAllAddress)
                        .broadcastAddress(Addresses.post(Addresses.add(flowParams.getEntity())))
                        .build()
                )
            );
        }

        private AddressAndHandler addMessageHandler(FlowParams flowParams) {

            String addAddress = Addresses.add(flowParams.getEntity());

            return addHandler(
                addAddress,
                flowBuilderHelper.addFlowHolder(
                    FlowBuilderHelper.AddParams.builder()
                        .module(module)
                        .entity(flowParams.getEntity())
                        .action(addAddress)
                        .broadcastAddress(Addresses.post(addAddress))
                        .build()
                )
            );
        }

        private AddressAndHandler addAllHandler(String messageAddress, AddAllFlowHolder addAllFlowHolder) {
            return consumer(
                messageAddress,
                new AddAllMessageHandlerBuilderImpl(
                    addAllFlowHolder,
                    module.require(FlowToJsonArrayMessageHandlerConverter.class)
                ).build()
            );
        }

        private AddressAndHandler findAllHandler(FindAllFlowHolder findAllFlowHolder) {
            return consumer(
                Addresses.findAll(Entities.USER_ENTITY),
                new FindAllMessageHandlerBuilderImpl(
                    findAllFlowHolder,
                    module.require(FlowToJsonObjectMessageHandlerConverter.class)
                ).build()
            );
        }

        private AddressAndHandler findOneHandler(FindOneFlowHolder findOneFlowHolder) {

            return consumer(
                Addresses.findOne(Entities.USER_ENTITY),
                new FindOneMessageHandlerBuilderImpl<Long>(
                    findOneFlowHolder,
                    module.require(FlowToJsonObjectMessageHandlerConverter.class)
                ).build()
            );

        }

        private AddressAndHandler addHandler(String messageAddress, AddFlowHolder addFlowHolder) {
            return consumer(
                messageAddress,
                new AddMessageHandlerBuilderImpl(
                    addFlowHolder,
                    module.require(FlowToJsonObjectMessageHandlerConverter.class)
                ).build()
            );
        }

        AddressAndHandler consumer(String address, MessageHandler messageHandler) {
            return new AddressAndHandler(
                address, messageHandler
            );
        }
    }
}
