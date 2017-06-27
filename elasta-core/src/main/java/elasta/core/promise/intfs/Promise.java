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

    Promise<T> err2(Error2Handler<T> errorHandler);

    Promise<T> errP(ErrorPHandler errorHandler);

    Promise<T> err2P(Error2PHandler<T> errorHandler);

    <P> Promise<T> errd(DoOnErrorHandler<P, T> doOnErrorHandler);

    <P> Promise<T> errdP(DoOnErrorPHandler<P, T> doOnErrorHandler);

    Promise<T> cmp(CompleteHandler<T> completeHandler);

    Promise<T> cmpP(CompletePHandler<T> completeHandler);

    boolean isComplete();

    boolean isFiltered();

    boolean isSuccess();

    boolean isError();

    Signal<T> signal();

    T val();

    T orElse(T t);

    Throwable err();
}
