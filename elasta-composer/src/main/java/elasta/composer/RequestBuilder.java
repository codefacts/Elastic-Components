package elasta.composer;

/**
 * Created by sohan on 5/15/2017.
 */
public interface RequestBuilder<T> {

    RequestBuilder<T> headers(Headers headers);

    RequestBuilder<T> context(Context context);

    RequestBuilder<T> body(T body);

    RequestBuilder<T> userId(Object userId);

    Msg<T> build();

}
