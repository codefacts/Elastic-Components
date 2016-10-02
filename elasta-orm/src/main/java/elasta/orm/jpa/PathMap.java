package elasta.orm.jpa;

import elasta.commons.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jango on 10/2/2016.
 */
public class PathMap {
    private final HashMap<String, Object> map;

    public PathMap(HashMap<String, Object> map) {
        this.map = map;
    }

    public Map<String, Object> get(List<String> key) {
        HashMap<String, Object> mp = map;

        for (String path : key) {

            if (Utils.not(mp.containsKey(path))) {
                HashMap<String, Object> hashMap = new HashMap<>();
                mp.put(path, hashMap);
                mp = hashMap;
                continue;
            }
            mp = (HashMap<String, Object>) mp.get(path);
        }

        return mp;
    }
}
