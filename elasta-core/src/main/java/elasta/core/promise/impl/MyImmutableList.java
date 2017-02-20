package elasta.core.promise.impl;

import java.util.AbstractList;
import java.util.List;
import java.util.RandomAccess;

/**
 * Created by Jango on 17/02/20.
 */
final public class MyImmutableList<T> extends AbstractList<T> implements List<T>, RandomAccess {
    final Object[] objects;

    public MyImmutableList(Object[] objects) {
        this.objects = objects;
    }

    @Override
    public T get(int index) {
        return (T) objects[index];
    }

    @Override
    public int size() {
        return objects.length;
    }
}
