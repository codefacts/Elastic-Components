package elasta.webutils;

import elasta.core.statemachine.StateMachine;

/**
 * Created by Jango on 9/14/2016.
 */
public class CrudStateMachines {
    private final StateMachine find;
    private final StateMachine findAll;
    private final StateMachine create;
    private final StateMachine updateAllProperties;
    private final StateMachine updateSomeProperties;
    private final StateMachine delete;

    public CrudStateMachines(StateMachine find, StateMachine findAll, StateMachine create, StateMachine updateAllProperties, StateMachine updateSomeProperties, StateMachine delete) {
        this.find = find;
        this.findAll = findAll;
        this.create = create;
        this.updateAllProperties = updateAllProperties;
        this.updateSomeProperties = updateSomeProperties;
        this.delete = delete;
    }

    public StateMachine getFind() {
        return find;
    }

    public StateMachine getFindAll() {
        return findAll;
    }

    public StateMachine getCreate() {
        return create;
    }

    public StateMachine getUpdateAllProperties() {
        return updateAllProperties;
    }

    public StateMachine getUpdateSomeProperties() {
        return updateSomeProperties;
    }

    public StateMachine getDelete() {
        return delete;
    }
}
