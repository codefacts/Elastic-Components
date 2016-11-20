package elasta.composer.pipeline.transformation.impl;

import elasta.composer.pipeline.transformation.Transformation;

import java.util.List;

/**
 * Created by shahadat on 5/11/16.
 */
public class TransformationImpl<T, R> implements Transformation<T, R> {
    private final List<Transformation> transformationList;

    public TransformationImpl(List<Transformation> transformationList) {
        this.transformationList = transformationList;
    }

    @Override
    public R transform(T first) {
        Object val = first;
        for (Transformation transformation : transformationList) {
            val = transformation.transform(val);
        }
        return (R) val;
    }
}
