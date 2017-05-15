package elasta.composer;

import com.google.common.collect.ListMultimap;
import elasta.composer.ex.NoUserInContextException;
import elasta.composer.impl.MsgImpl;

import java.util.Map;

/**
 * Created by sohan on 5/15/2017.
 */
public interface Msg<T> {

    Headers headers();

    Context context();

    T body();

    default <R> R userId() {
        throw new NoUserInContextException("No user exists in the Msg context");
    }

    Msg<T> addHeaders(ListMultimap<String, String> multimap);

    Msg<T> addHeaders(Map<String, String> map);

    Msg<T> addContext(Map<String, Object> map);

    Msg<T> withBody(T body);

    static <TT> RequestBuilder<TT> builder() {
        return new MsgImpl.RequestImplBuilder<>();
    }

    static <TT> RequestBuilder<TT> builder(Msg<TT> msg) {
        return new MsgImpl.RequestImplBuilder<>(msg);
    }
}
