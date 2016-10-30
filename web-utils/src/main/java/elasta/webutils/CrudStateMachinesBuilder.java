package elasta.webutils;

import elasta.core.flow.Flow;

public class CrudStateMachinesBuilder {
    private Flow find;
    private Flow findAll;
    private Flow create;
    private Flow updateAllProperties;
    private Flow updateSomeProperties;
    private Flow delete;

    public CrudStateMachinesBuilder setFind(Flow find) {
        this.find = find;
        return this;
    }

    public CrudStateMachinesBuilder setFindAll(Flow findAll) {
        this.findAll = findAll;
        return this;
    }

    public CrudStateMachinesBuilder setCreate(Flow create) {
        this.create = create;
        return this;
    }

    public CrudStateMachinesBuilder setUpdateAllProperties(Flow updateAllProperties) {
        this.updateAllProperties = updateAllProperties;
        return this;
    }

    public CrudStateMachinesBuilder setUpdateSomeProperties(Flow updateSomeProperties) {
        this.updateSomeProperties = updateSomeProperties;
        return this;
    }

    public CrudStateMachinesBuilder setDelete(Flow delete) {
        this.delete = delete;
        return this;
    }

    public CrudStateMachines createCrudStateMachines() {
        return new CrudStateMachines(find, findAll, create, updateAllProperties, updateSomeProperties, delete);
    }
}