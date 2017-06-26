package elasta.composer.model.response;

import com.google.common.collect.ImmutableList;
import elasta.orm.query.expression.PathExpression;

/**
 * Created by sohan on 5/14/2017.
 */
public interface ValidationResultModel {
    String field = "column";
    String value = "value";
    String validationErrorCode = "validationErrorCode";
    String message = "message";

    static void main(String[] asdf) {
        System.out.println("path: " + PathExpression.create(ImmutableList.of()));
    }
}
