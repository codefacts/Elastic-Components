package elasta.orm.nm.delete.impl;

/**
 * Created by Jango on 17/02/07.
 */
public class BelongsToDeleteDependency {
    private DirectDeleteHandler deleteHandler;
    private String fieldName;

    public DirectDeleteHandler getDeleteHandler() {
        return deleteHandler;
    }

    public void setDeleteHandler(DirectDeleteHandler deleteHandler) {
        this.deleteHandler = deleteHandler;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
