package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.model.response.builder.ValidationErrorModelBuilder;
import elasta.composer.state.handlers.ValidateStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class ValidateStateHandlerBuilderImpl implements ValidateStateHandlerBuilder {
    final String entity;
    final JsonObjectValidatorAsync jsonObjectValidatorAsync;
    final ValidationErrorModelBuilder validationErrorModelBuilder;

    public ValidateStateHandlerBuilderImpl(String entity, JsonObjectValidatorAsync jsonObjectValidatorAsync, ValidationErrorModelBuilder validationErrorModelBuilder) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(jsonObjectValidatorAsync);
        Objects.requireNonNull(validationErrorModelBuilder);
        this.entity = entity;
        this.jsonObjectValidatorAsync = jsonObjectValidatorAsync;
        this.validationErrorModelBuilder = validationErrorModelBuilder;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return msg -> jsonObjectValidatorAsync.validate(msg.body())
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
