package elasta.promise.intfs;

/**
 * Created by Shahadat on 8/24/2016.
 */
public interface ErrorPHandler extends Invokable {
    Promise<Void> apply(Throwable e) throws Throwable;
}
