package elasta.composer.pipeline.validator.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.ValidatorAsync;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.List;

/**
 * Created by shahadat on 4/27/16.
 */
public class ValidationPipelineAsync<T> implements ValidatorAsync<T> {
    private final List<ValidatorAsync<T>> list;

    public ValidationPipelineAsync(List<ValidatorAsync<T>> list) {
        this.list = list;
    }

    public Promise<List<ValidationResult>> validate(T jsonObject) {

        if (list.isEmpty()) return Promises.just(null);

        final ImmutableList.Builder<ValidationResult> builder = ImmutableList.<ValidationResult>builder();

        Promise<List<ValidationResult>> promise = list.get(0).validate(jsonObject)
            .then(val -> {
                if (val != null) {
                    builder.addAll(val);
                }
            });

        for (int i = 1; i < list.size(); i++) {
            final ValidatorAsync<T> validatorAsync = list.get(i);
            promise = promise
                .mapP(result -> validatorAsync.validate(jsonObject))
                .then(result -> {
                    if (result != null) {
                        builder.addAll(result);
                    }
                });
        }

        return promise.map(v -> {
            final ImmutableList<ValidationResult> results = builder.build();
            return results.size() <= 0 ? null : results;
        });
    }
}
