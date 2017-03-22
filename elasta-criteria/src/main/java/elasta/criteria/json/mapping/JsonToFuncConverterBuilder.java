package elasta.criteria.json.mapping;

import elasta.criteria.Func;
import io.vertx.core.json.JsonArray;

/**
 * Created by sohan on 3/20/2017.
 */
public interface JsonToFuncConverterBuilder {

    JsonToFuncConverter op1(Operation1Builder builder);

    JsonToFuncConverter op2(Operation2Builder builder);

    JsonToFuncConverter op3(Operation3Builder builder);

    JsonToFuncConverter arrayOp(ArrayOperationBuilder opsBuilder);
}
