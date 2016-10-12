package elasta.orm.jpa;

import com.google.common.collect.ImmutableMap;
import elasta.orm.jpa.models.ModelInfo;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Type;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Jango on 10/12/2016.
 */
public class JpaUtils {
    public static Map<String, Class> entityClassMap(EntityManagerFactory emf) {

        return ImmutableMap.copyOf(
            emf.getMetamodel().getEntities().stream()
                .collect(Collectors.toMap(EntityType::getName, Type::getJavaType))
        );
    }

    public static Map<String, ModelInfo> modelInfoByModelMap(EntityManagerFactory emf) {
        return new ModelInfoMapHelper().modelInfoByModelMap(emf);
    }
}
