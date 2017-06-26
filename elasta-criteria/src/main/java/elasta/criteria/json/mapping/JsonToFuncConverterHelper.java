package elasta.criteria.json.mapping;

/**
 * Created by sohan on 6/27/2017.
 */
public interface JsonToFuncConverterHelper {

    JsonToFuncConverter op1(Operation1Builder builder);

    JsonToFuncConverter op2(Operation2Builder builder);

    JsonToFuncConverter op3(Operation3Builder builder);

    JsonToFuncConverter arrayOp(ArrayOperationBuilder opsBuilder);

}
