package elasta.orm.nm.delete.dependency;

import java.util.Collection;

/**
 * Created by sohan on 3/5/2017.
 */
public interface DependencyDataLoaderGraph {
    Collection<DependencyDataLoader> get(String table);
}
