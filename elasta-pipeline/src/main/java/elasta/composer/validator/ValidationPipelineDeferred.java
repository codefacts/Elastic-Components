package elasta.composer.validator;

import com.google.common.collect.ImmutableList;
import elasta.composer.util.Context;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.List;

/**
 * Created by shahadat on 4/27/16.
 */
public class ValidationPipelineDeferred<T> {
    private final List<ValidatorDeferred<T>> list;

    public ValidationPipelineDeferred(List<ValidatorDeferred<T>> list) {
        this.list = list;
    }

    public Promise<List<ValidationResult>> validate(T jsonObject, Context context) {

        if (list.isEmpty()) return Promises.just(null);

        final ImmutableList.Builder<ValidationResult> builder = ImmutableList.<ValidationResult>builder();

        Promise<ValidationResult> promise = list.get(0).validate(jsonObject, context)
            .then(val -> {
                if (val != null) {
                    builder.add(val);
                }
            });

        for (int i = 1; i < list.size(); i++) {
            final ValidatorDeferred<T> validatorDeferred = list.get(i);
            promise = promise
                .mapP(result -> validatorDeferred.validate(jsonObject, context))
                .then(result -> {
                    if (result != null) {
                        builder.add(result);
                    }
                });
        }

        return promise.map(v -> {
            final ImmutableList<ValidationResult> results = builder.build();
            return results.size() <= 0 ? null : results;
        });
    }
}
