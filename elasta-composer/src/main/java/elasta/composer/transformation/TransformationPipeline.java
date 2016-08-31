package elasta.composer.transformation;

import java.util.List;

/**
 * Created by shahadat on 5/11/16.
 */
public class TransformationPipeline<T, R> implements Transform<T, R> {
    private final List<Transform> transformList;

    public TransformationPipeline(List<Transform> transformList) {
        this.transformList = transformList;
    }

    @Override
    public R transform(T first) {
        Object val = first;
        for (Transform transform : transformList) {
            val = transform.transform(val);
        }
        return (R) val;
    }
}
