package elasta.composer.interceptor;

import elasta.composer.Msg;
import elasta.sql.core.UpdateTpl;

/**
 * Created by sohan on 7/8/2017.
 */
@FunctionalInterface
public interface DbOperationInterceptor {
    <T> UpdateTpl intercept(UpdateTpl updateTpl, Msg<T> msg);
}
