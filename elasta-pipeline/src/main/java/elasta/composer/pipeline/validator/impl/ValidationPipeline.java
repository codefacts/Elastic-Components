package elasta.composer.pipeline.validator.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.pipeline.validator.ValidationResult;
import elasta.composer.pipeline.validator.Validator;

import java.util.List;

/**
 * Created by shahadat on 2/28/16.
 */
public class ValidationPipeline<T> implements Validator<T> {

    private final List<Validator<T>> validatorList;

    public ValidationPipeline(List<Validator<T>> validatorList) {
        this.validatorList = validatorList;
    }

    public List<ValidationResult> validate(T obj) {
        final ImmutableList.Builder<ValidationResult> builder = ImmutableList.builder();

        validatorList.forEach(tValidator -> {
            final List<ValidationResult> result = tValidator.validate(obj);
            if (result != null) builder.addAll(result);
        });

        final ImmutableList<ValidationResult> list = builder.build();
        return list.size() <= 0 ? null : list;
    }
}
