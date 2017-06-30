package tracker;

import elasta.orm.query.expression.FieldExpression;

import java.util.Collection;
import java.util.List;

/**
 * Created by sohan on 6/30/2017.
 */
public interface AppHelpers {

    List<FieldExpression> findOneFields(String entity);

    List<FieldExpression> findAllFields(String entity);
}
