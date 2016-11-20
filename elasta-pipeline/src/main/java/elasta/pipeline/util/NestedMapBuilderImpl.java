package elasta.pipeline.util;

import java.util.*;
import java.util.stream.Collectors;

import static elasta.commons.Utils.not;

/**
 * Created by Jango on 2016-11-19.
 */
public class NestedMapBuilderImpl<T> implements NestedMapBuilder<T> {
    private static final String ARR = "[]";
    private final Map<String, Object> rootMap = createMap();
    private final Object EMPTY_OBJECT = null;
    private final int MAX_LIST_SIZE;

    public NestedMapBuilderImpl() {
        MAX_LIST_SIZE = 250;
    }

    public NestedMapBuilderImpl(int max_list_size) {
        MAX_LIST_SIZE = max_list_size;
    }

    @Override
    public NestedMapBuilder<T> put(String name, T value) {
        if (name.trim().isEmpty()) {
            return this;
        }
        List<String> path = toPath(name);

        boolean isListParent = false;
        int lastIndex = -1;
        Object mm = rootMap;
        for (int i = 0, pathSize_1 = path.size() - 1; i < pathSize_1; i++) {
            String part = path.get(i);

            if (isListParent) {

                Tpl tpl = handleListParent(part, (List) mm, lastIndex);

                mm = tpl.object;
                isListParent = tpl.isListParent;
                lastIndex = tpl.lastIndex;

                continue;
            }

            Map<String, Object> parent = (Map<String, Object>) mm;

            Tpl tpl = handleMapParent(part, parent);

            mm = tpl.object;
            isListParent = tpl.isListParent;
            lastIndex = tpl.lastIndex;
        }

        String lastPart = path.get(path.size() - 1);

        IndexAndKey indexAndKey = listIndex(lastPart);
        int index = indexAndKey.index;

        if (isListParent) {

            List parentList = (List) mm;

            if (index >= 0) {

                List<Object> list = handleListPart(parentList, indexAndKey.key, lastIndex);

                set(list, index, value);

                return this;
            }

            Map mpp = get(parentList, lastIndex);

            if (mpp == null) {

                mpp = createMap();

                set(parentList, lastIndex, mpp);
            }

            mpp.put(lastPart, value);

            return this;
        }

        Map mpp = (Map) mm;

        if (index >= 0) {

            List list = (List) mpp.get(indexAndKey.key);

            if (list == null) {

                list = createList();

                mpp.put(indexAndKey.key, list);
            }

            set(list, index, value);

            return this;
        }

        mpp.put(path.get(path.size() - 1), value);

        return this;
    }

    private Tpl handleMapParent(String part, Map<String, Object> parent) {

        IndexAndKey indexAndKey = listIndex(part);

        if (indexAndKey.index >= 0) {

            String key = indexAndKey.key;

            Object pp = parent.get(key);

            if (pp == null) {
                pp = createList();
                parent.put(key, pp);
            }

            return new Tpl(pp, indexAndKey.index, true);

        } else {

            Object pp = parent.get(part);

            if (pp == null) {
                pp = createMap();
                parent.put(part, pp);
            }

            return new Tpl(pp, -1, false);
        }
    }

    private Tpl handleListParent(String part, List parent, final int lastIndex) {

        IndexAndKey indexAndKey = listIndex(part);
        int index = indexAndKey.index;

        if (index >= 0) {

            List<Object> list = handleListPart(parent, indexAndKey.key, lastIndex);
            return new Tpl(
                list, index, true);

        } else {

            Map<String, Object> mpp = this.get(parent, lastIndex);

            if (mpp == null) {

                mpp = createMap();

                this.set(parent, lastIndex, mpp);
            }

            Map<String, Object> mkk = (Map<String, Object>) mpp.get(part);

            if (mkk == null) {

                mkk = createMap();

                mpp.put(part, mkk);
            }

            return new Tpl(mkk, index, false);
        }

    }

    private List<Object> handleListPart(List parent, String key, int lastIndex) {

        if (key.isEmpty()) {

            List list = this.get(parent, lastIndex);

            if (list == null) {

                list = createList();
                parent.set(lastIndex, list);
            }

            return list;

        } else {

            Map mpp = this.get(parent, lastIndex);

            if (mpp == null) {

                mpp = createMap();

                set(parent, lastIndex, mpp);
            }

            List list = (List) mpp.get(key);

            if (list == null) {

                list = createList();

                mpp.put(key, list);
            }

            return list;
        }
    }

    private boolean isInBound(int size, int index) {
        return index - (size - 1) <= 0;
    }

    private IndexAndKey listIndex(String part) {
        int startIndex = part.indexOf('[');
        int lastIndex = part.lastIndexOf(']');
        if (startIndex >= 0 && lastIndex >= 0 && startIndex < lastIndex) {
            return new IndexAndKey(
                Integer.parseInt(part.substring(startIndex + 1, lastIndex).trim()),
                part.substring(0, startIndex)
            );
        }
        return new IndexAndKey(-1, null);
    }

    @Override
    public Map<String, Object> createMap() {
        return new LinkedHashMap<>();
    }

    @Override
    public List<Object> createList() {
        return new ArrayList<>();
    }

    @Override
    public Map<String, Object> build() {
        return rootMap;
    }

    private List<String> toPath(String name) {
        return Arrays.asList(name.split("\\.")).stream().map(String::trim).filter((s) -> not(s.isEmpty())).collect(Collectors.toList());
    }

    private <T> T get(List list, int index) {

        if (index > MAX_LIST_SIZE) {
            throw new NestedMapException("Index exceeds max size limit. Index = " + index);
        }

        int diff = index - (list.size() - 1);
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                list.add(EMPTY_OBJECT);
            }
        }
        return (T) list.get(index);
    }

    private void set(List list, int index, Object value) {

        if (index > MAX_LIST_SIZE) {
            throw new NestedMapException("Index exceeds max size limit. Index = " + index);
        }

        int diff = index - (list.size() - 1);
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                list.add(EMPTY_OBJECT);
            }
        }
        list.set(index, value);
    }

    public static void main(String[] args) {

        NestedMapBuilderImpl<Object> builder = new NestedMapBuilderImpl<>();

        Map<String, Object> build = builder.put("x[].m.z[].k", "sohan").put("x[].m", "kaka").put("x[]", "sona").put("x[].m.z[].tt", "lala").build();

        System.out.println(build);
    }

    private class Tpl {
        final Object object;
        final int lastIndex;
        final boolean isListParent;

        Tpl(Object object, int lastIndex, boolean isListParent) {
            this.object = object;
            this.lastIndex = lastIndex;
            this.isListParent = isListParent;
        }
    }

    private class IndexAndKey {
        final int index;
        final String key;

        private IndexAndKey(int index, String key) {
            this.index = index;
            this.key = key;
        }
    }
}
