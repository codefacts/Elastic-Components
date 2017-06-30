package tracker.impl;

import com.google.common.collect.ImmutableList;
import elasta.authorization.Authorizer;
import elasta.composer.MessageBus;
import elasta.composer.converter.JsonObjectToPageRequestConverter;
import elasta.composer.converter.JsonObjectToQueryParamsConverter;
import elasta.composer.flow.builder.impl.*;
import elasta.composer.flow.holder.*;
import elasta.composer.model.response.builder.*;
import elasta.composer.respose.generator.ResponseGenerator;
import elasta.composer.state.handlers.*;
import elasta.composer.state.handlers.impl.*;
import elasta.core.promise.impl.Promises;
import elasta.module.ModuleSystem;
import elasta.orm.Orm;
import elasta.orm.idgenerator.ObjectIdGenerator;
import elasta.orm.query.expression.FieldExpression;
import elasta.orm.query.expression.impl.FieldExpressionImpl;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import elasta.sql.SqlDB;
import tracker.App;
import tracker.AppHelpers;
import tracker.FlowBuilderHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/30/2017.
 */
final public class FlowBuilderHelperImpl implements FlowBuilderHelper {

    @Override
    public AddFlowHolder addFlowHolder(AddParams params) {
        ModuleSystem module = params.getModule();
        return new AddFlowHolder(
            new AddFlowBuilderImpl(
                startStateHandler(module),
                authorizeStateHandler(
                    module, params.getAction()
                ),
                generateIdStateHandler(module, params.getEntity()),
                validateStateHandler(module, params.getEntity()),
                addStateHandler(module, params.getEntity()),
                broadcastStateHandler(module.require(MessageBus.class), params.getBroadcastAddress()),
                generateResponseStateHandler(module),
                endStateHandler(module)
            ).build()
        );
    }

    @Override
    public AddAllFlowHolder addAllFlowHolder(AddAllParams params) {
        ModuleSystem module = params.getModule();
        return new AddAllFlowHolder(
            new AddAllFlowBuilderImpl(
                startStateHandler(module),
                authorizeAllStateHandler(module, params.getAction()),
                generateIdsAllStateHandler(module, params.getEntity()),
                validateAllStateHandler(module, params.getEntity()),
                addAllStateHandler(module, params.getEntity()),
                broadcastAllStateHandler(module, params.getBroadcastAddress()),
                generateResponseStateHandler(module),
                endStateHandler(module)
            ).build()
        );
    }

    @Override
    public UpdateFlowHolder updateFlowHolder(UpdateParams params) {
        ModuleSystem module = params.getModule();
        return new UpdateFlowHolder(
            new UpdateFlowBuilderImpl(
                startStateHandler(module),
                authorizeStateHandler(
                    module, params.getAction()
                ),
                generateIdStateHandler(module, params.getEntity()),
                validateStateHandler(module, params.getEntity()),
                updateStateHandler(module, params.getEntity()),
                broadcastStateHandler(module.require(MessageBus.class), params.getBroadcastAddress()),
                generateResponseStateHandler(module),
                endStateHandler(module)
            ).build()
        );
    }

    @Override
    public UpdateAllFlowHolder updateAllFlowHolder(UpdateAllParams params) {
        final ModuleSystem module = params.getModule();
        return new UpdateAllFlowHolder(
            new UpdateAllFlowBuilderImpl(
                startStateHandler(module),
                authorizeAllStateHandler(module, params.getAction()),
                generateIdsAllStateHandler(module, params.getEntity()),
                validateAllStateHandler(module, params.getEntity()),
                updateAllStateHandler(module, params.getEntity()),
                broadcastAllStateHandler(module, params.getBroadcastAddress()),
                generateResponseStateHandler(module),
                endStateHandler(module)
            ).build()
        );
    }

    @Override
    public DeleteFlowHolder deleteFlowHolder(DeleteParams params) {
        final ModuleSystem module = params.getModule();
        return new DeleteFlowHolder(
            new DeleteFlowBuilderImpl(
                startStateHandler(module),
                authorizeStateHandler(module, params.getAction()),
                deleteStateHandler(module, params.getEntity()),
                broadcastStateHandler(module.require(MessageBus.class), params.getBroadcastAddress()),
                generateResponseStateHandler(module),
                endStateHandler(module)
            ).build()
        );
    }

    @Override
    public DeleteAllFlowHolder deleteAllFlowHolder(DeleteAllParams params) {
        final ModuleSystem module = params.getModule();

        return new DeleteAllFlowHolder(
            new DeleteAllFlowBuilderImpl(
                startStateHandler(module),
                authorizeAllStateHandler(module, params.getAction()),
                deleteAllStateHandler(module, params.getEntity()),
                broadcastAllStateHandler(module, params.getBroadcastAddress()),
                generateResponseStateHandler(module),
                endStateHandler(module)
            ).build()
        );
    }

    @Override
    public FindOneFlowHolder findOneFlowHolder(FindOneParams params) {

        final ModuleSystem module = params.getModule();

        return new FindOneFlowHolder(
            new FindOneFlowBuilderImpl(
                startStateHandler(module),
                authorizeStateHandler(module, params.getAction()),
                conversionToCriteriaStateHandler(module),
                findOneStateHandler(
                    module,
                    params.getEntity(),
                    module.require(App.Config.class).getRootAlias()
                ),
                endStateHandler(module)
            ).build()
        );
    }

    private AddStateHandler addStateHandler(ModuleSystem module, String entity) {
        return new AddStateHandlerImpl(
            entity,
            module.require(Orm.class),
            module.require(SqlDB.class)
        );
    }

    private FindOneStateHandler findOneStateHandler(ModuleSystem module, String entity, String rootAlias) {
        return new FindOneStateHandlerImpl(
            rootAlias, entity, fieldSelections(module, entity, rootAlias),
            module.require(Orm.class)
        );
    }

    private ConversionToCriteriaStateHandler conversionToCriteriaStateHandler(ModuleSystem module) {
        return module.require(ConversionToCriteriaStateHandler.class);
    }

    @Override
    public FindAllFlowHolder findAllFlowHolder(FindAllParams params) {

        ModuleSystem module = params.getModule();

        return new FindAllFlowHolder(
            new FindAllFlowBuilderImpl(
                startStateHandler(module),
                authorizeStateHandler(module, params.getAction()),
                findAllStateHandler(module, params.getEntity(), params.getPaginationKey()),
                endStateHandler(module)
            ).build()
        );
    }

    private FindAllStateHandler findAllStateHandler(ModuleSystem module, String entity, FieldExpression paginationKey) {
        return new FindAllStateHandlerImpl(
            entity,
            module.require(App.Config.class).getRootAlias(),
            module.require(AppHelpers.class).findAllFields(entity),
            paginationKey,
            module.require(Orm.class),
            module.require(JsonObjectToPageRequestConverter.class),
            module.require(JsonObjectToQueryParamsConverter.class),
            module.require(PageModelBuilder.class)
        );
    }

    private DeleteAllStateHandler deleteAllStateHandler(ModuleSystem module, String entity) {
        return new DeleteAllStateHandlerImpl(
            module.require(Orm.class),
            module.require(SqlDB.class),
            entity
        );
    }

    private DeleteStateHandler deleteStateHandler(ModuleSystem module, String entity) {
        return new DeleteStateHandlerImpl(
            module.require(Orm.class),
            module.require(SqlDB.class),
            entity
        );
    }

    private UpdateAllStateHandler updateAllStateHandler(ModuleSystem module, String entity) {
        return new UpdateAllStateHandlerImpl(
            entity,
            module.require(Orm.class),
            module.require(SqlDB.class)
        );
    }

    private UpdateStateHandler updateStateHandler(ModuleSystem module, String entity) {
        return new UpdateStateHandlerImpl(
            entity, module.require(Orm.class), module.require(SqlDB.class)
        );
    }

    private BroadcastAllStateHandlerImpl broadcastAllStateHandler(ModuleSystem module, String broadcastAddress) {
        return new BroadcastAllStateHandlerImpl(
            module.require(MessageBus.class),
            broadcastAddress
        );
    }

    private AddAllStateHandler addAllStateHandler(ModuleSystem module, String entity) {
        return new AddAllStateHandlerImpl(
            entity, module.require(SqlDB.class), module.require(Orm.class)
        );
    }

    private ValidateAllStateHandler validateAllStateHandler(ModuleSystem module, String entity) {
        return new ValidateAllStateHandlerImpl(
            entity,
            asyncUserValidator(),
            module.require(ValidationErrorModelBuilder.class),
            module.require(ValidationSuccessModelBuilder.class)
        );
    }

    private GenerateIdsAllStateHandler generateIdsAllStateHandler(ModuleSystem module, String entity) {
        return new GenerateIdsAllStateHandlerImpl(
            entity, module.require(ObjectIdGenerator.class)
        );
    }

    private AuthorizeAllStateHandler authorizeAllStateHandler(ModuleSystem module, String action) {
        return new AuthorizeAllStateHandlerImpl(
            module.require(Authorizer.class),
            action,
            module.require(AuthorizationErrorModelBuilder.class),
            module.require(AuthorizationSuccessModelBuilder.class)
        );
    }

    private StartStateHandler startStateHandler(ModuleSystem module) {
        return module.require(StartStateHandler.class);
    }

    private GenerateIdStateHandler generateIdStateHandler(ModuleSystem module, String entity) {
        return new GenerateIdStateHandlerImpl(
            entity, module.require(ObjectIdGenerator.class)
        );
    }

    private EndStateHandler endStateHandler(ModuleSystem module) {
        return module.require(EndStateHandler.class);
    }

    private GenerateResponseStateHandler generateResponseStateHandler(ModuleSystem module) {
        return new GenerateResponseStateHandlerImpl(
            module.require(ResponseGenerator.class)
        );
    }

    private BroadcastStateHandler broadcastStateHandler(MessageBus messageBus, String broadcastAddress) {
        return new BroadcastStateHandlerImpl(
            messageBus,
            broadcastAddress
        );
    }

    private ValidateStateHandler validateStateHandler(ModuleSystem module, String entity) {
        return new ValidateStateHandlerImpl(
            entity,
            asyncUserValidator(),
            module.require(ValidationErrorModelBuilder.class)
        );
    }

    private AuthorizeStateHandler authorizeStateHandler(ModuleSystem module, String action) {
        return new AuthorizeStateHandlerImpl(
            module.require(Authorizer.class),
            action,
            module.require(AuthorizationErrorModelBuilder.class)
        );
    }

    private List<FieldExpression> fieldSelections(ModuleSystem module, String entity, String rootAlias) {
        return ImmutableList.copyOf(
            module.require(AppHelpers.class).findOneFields(entity)
        );
    }

    private JsonObjectValidatorAsync asyncUserValidator() {
        return val -> Promises.of(ImmutableList.of());
    }
}
