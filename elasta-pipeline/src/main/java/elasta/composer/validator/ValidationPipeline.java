package elasta.composer.validator;

import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Created by shahadat on 2/28/16.
 */
public class ValidationPipeline<T> {

    private final List<Validator<T>> validatorList;

    public ValidationPipeline(List<Validator<T>> validatorList) {
        this.validatorList = validatorList;
    }

    public List<ValidationResult> validate(T obj) {
        final ImmutableList.Builder<ValidationResult> builder = ImmutableList.builder();

        validatorList.forEach(tValidator -> {
            final ValidationResult result = tValidator.validate(obj);
            if (result != null) builder.add(result);
        });

        final ImmutableList<ValidationResult> list = builder.build();
        return list.size() <= 0 ? null : list;
    }
}
