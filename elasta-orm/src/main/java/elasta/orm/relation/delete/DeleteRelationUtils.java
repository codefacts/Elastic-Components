package elasta.orm.relation.delete;

import elasta.orm.entity.core.DbMapping;
import elasta.orm.entity.core.columnmapping.RelationMapping;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Created by sohan on 4/14/2017.
 */
public interface DeleteRelationUtils {
    static Stream<RelationMapping> listChildMappings(DbMapping dbMapping) {
        return Arrays.stream(dbMapping.getColumnMappings())
            .filter(dbColumnMapping -> dbColumnMapping instanceof RelationMapping)
            .map(dbColumnMapping -> (RelationMapping) dbColumnMapping)
            ;
    }
}
