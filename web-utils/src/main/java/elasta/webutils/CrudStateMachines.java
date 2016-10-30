package elasta.webutils;

import elasta.core.flow.Flow;

/**
 * Created by Jango on 9/14/2016.
 */
public class CrudStateMachines {
    private final Flow find;
    private final Flow findAll;
    private final Flow create;
    private final Flow updateAllProperties;
    private final Flow updateSomeProperties;
    private final Flow delete;

    public CrudStateMachines(Flow find, Flow findAll, Flow create, Flow updateAllProperties, Flow updateSomeProperties, Flow delete) {
        this.find = find;
        this.findAll = findAll;
        this.create = create;
        this.updateAllProperties = updateAllProperties;
        this.updateSomeProperties = updateSomeProperties;
        this.delete = delete;
    }

    public Flow getFind() {
        return find;
    }

    public Flow getFindAll() {
        return findAll;
    }

    public Flow getCreate() {
        return create;
    }

    public Flow getUpdateAllProperties() {
        return updateAllProperties;
    }

    public Flow getUpdateSomeProperties() {
        return updateSomeProperties;
    }

    public Flow getDelete() {
        return delete;
    }
}
