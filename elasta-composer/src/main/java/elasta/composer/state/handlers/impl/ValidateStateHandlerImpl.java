package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.state.handlers.ValidateStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class ValidateStateHandlerImpl implements ValidateStateHandler<JsonObject, JsonObject> {
    final String entity;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;

    public ValidateStateHandlerImpl(String entity, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(jsonObjectValidatorAsync);
        Objects.requireNonNull(validationErrorModelBuilder);
        this.entity = entity;
        this.jsonObjectValidatorAsync = jsonObjectValidatorAsync;
        this.validationErrorModelBuilder = validationErrorModelBuilder;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {

        return jsonObjectValidatorAsync.validate(msg.body())
            .map(validationResults -> {
                if (validationResults.size() > 0) {
                    return Flow.trigger(
                        Events.validationError,
                        msg.withBody(
                            validationErrorModelBuilder.build(
                                ValidationErrorModelBuilder.BuildParams.builder()
                                    .entity(entity)
                                    .validationErrors(validationResults)
                                    .build()
                            )
                        )
                    );
                }
                return Flow.trigger(Events.next, msg);
            });
    }
}
