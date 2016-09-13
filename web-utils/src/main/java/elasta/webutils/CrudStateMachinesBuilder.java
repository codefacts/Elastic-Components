package elasta.webutils;

import elasta.core.statemachine.StateMachine;

public class CrudStateMachinesBuilder {
    private StateMachine find;
    private StateMachine findAll;
    private StateMachine create;
    private StateMachine updateAllProperties;
    private StateMachine updateSomeProperties;
    private StateMachine delete;

    public CrudStateMachinesBuilder setFind(StateMachine find) {
        this.find = find;
        return this;
    }

    public CrudStateMachinesBuilder setFindAll(StateMachine findAll) {
        this.findAll = findAll;
        return this;
    }

    public CrudStateMachinesBuilder setCreate(StateMachine create) {
        this.create = create;
        return this;
    }

    public CrudStateMachinesBuilder setUpdateAllProperties(StateMachine updateAllProperties) {
        this.updateAllProperties = updateAllProperties;
        return this;
    }

    public CrudStateMachinesBuilder setUpdateSomeProperties(StateMachine updateSomeProperties) {
        this.updateSomeProperties = updateSomeProperties;
        return this;
    }

    public CrudStateMachinesBuilder setDelete(StateMachine delete) {
        this.delete = delete;
        return this;
    }

    public CrudStateMachines createCrudStateMachines() {
        return new CrudStateMachines(find, findAll, create, updateAllProperties, updateSomeProperties, delete);
    }
}