package elasta.core.promise.intfs;

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

    Promise<T> err(ErrorHandler errorHandler);

    Promise<T> errP(ErrorPHandler errorHandler);

    Promise<T> errd(DoOnErrorHandler<T> doOnErrorHandler);

    Promise<T> errdP(DoOnErrorPHandler<T> doOnErrorHandler);

    Promise<T> cmp(CompleteHandler<T> completeHandler);

    Promise<T> cmpP(CompletePHandler<T> completeHandler);

    boolean isComplete();

    boolean isFiltered();

    boolean isSuccess();

    boolean isError();

    T val();

    T orElse(T t);

    Throwable err();
}
