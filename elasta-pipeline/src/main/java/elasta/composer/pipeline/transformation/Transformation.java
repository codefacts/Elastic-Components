package elasta.composer.pipeline.transformation;

/**
 * Created by shahadat on 3/1/16.
 */
public interface Transformation<T, R> {
    R transform(T val);
}
