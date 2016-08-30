package elasta.promise.intfs;

/**
 * Created by someone on 15/10/2015.
 */
public interface Promise<T> {

    Promise<T> filter(FilterHandler<T> predicateUnchecked);
    Promise<T> filterP(FilterPHandler<T> predicateUnchecked);

    <R> Promise<R> map(MapHandler<T, R> functionUnchecked);
    <R> Promise<R> mapP(MapPHandler<T, R> function);

    Promise<T> then(ThenHandler<T> thenHandler);
    Promise<T> thenP(ThenPHandler<T> thenHandler);

    Promise<T> error(ErrorHandler errorHandler);
    Promise<T> errorP(ErrorPHandler errorHandler);

    Promise<T> complete(CompleteHandler<T> completeHandler);
    Promise<T> completeP(CompletePHandler<T> completeHandler);

    boolean isComplete();

    boolean isFiltered();

    boolean isSuccess();

    boolean isError();

    T value();

    T orElse(T t);

    Throwable error();
}
