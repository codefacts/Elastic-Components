package elasta.orm.nm.delete;

import elasta.orm.nm.delete.IndirectDeleteHandler;

/**
 * Created by Jango on 17/02/07.
 */
final public class IndirectDeleteDependency {
    private String fieldName;
    private IndirectDeleteHandler deleteHandler;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public IndirectDeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(IndirectDeleteHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }
}
