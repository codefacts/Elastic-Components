package elasta.pipeline.validator.impl;

import com.google.common.collect.ImmutableList;
import elasta.pipeline.validator.JsonObjectValidatorAsync;
import elasta.pipeline.validator.ValidationResult;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * Created by Jango on 2016-11-20.
 */
public class JsonObjectValidationPipelineAsync implements JsonObjectValidatorAsync {
    private final List<JsonObjectValidatorAsync> list;

    public JsonObjectValidationPipelineAsync(List<JsonObjectValidatorAsync> list) {
        this.list = list;
    }

    public Promise<List<ValidationResult>> validate(JsonObject jsonObject) {

        if (list.isEmpty()) return Promises.of(null);

        final ImmutableList.Builder<ValidationResult> builder = ImmutableList.<ValidationResult>builder();

        Promise<List<ValidationResult>> promise = list.get(0).validate(jsonObject)
            .then(val -> {
                if (val != null) {
                    builder.addAll(val);
                }
            });

        for (int i = 1; i < list.size(); i++) {
            final JsonObjectValidatorAsync validatorAsync = list.get(i);
            promise = promise
                .mapP(result -> validatorAsync.validate(jsonObject))
                .then(builder::addAll);
        }

        return promise.map(v -> {
            final ImmutableList<ValidationResult> results = builder.build();
            return results;
        });
    }
}
