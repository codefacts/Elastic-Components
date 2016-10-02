package elasta.orm.json.core;

import elasta.orm.json.sql.core.JavaType;

public class ModelPropBuilder {
    private String name;
    private String column;
    private JavaType javaType;
    private JoinInfo joinInfo;

    public ModelPropBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ModelPropBuilder setColumn(String column) {
        this.column = column;
        return this;
    }

    public ModelPropBuilder setJavaType(JavaType javaType) {
        this.javaType = javaType;
        return this;
    }

    public ModelPropBuilder setJoinInfo(JoinInfo joinInfo) {
        this.joinInfo = joinInfo;
        return this;
    }

    public ModelProp createModelProp() {
        return new ModelProp(name, column, javaType, joinInfo);
    }
}