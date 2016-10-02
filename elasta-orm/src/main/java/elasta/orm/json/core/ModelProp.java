package elasta.orm.json.core;

import elasta.orm.json.sql.core.JavaType;

/**
 * Created by Jango on 10/2/2016.
 */
public class ModelProp {
    private final String name;
    private final String column;
    private final JavaType javaType;
    private final JoinInfo joinInfo;

    public ModelProp(String name, String column, JavaType javaType, JoinInfo joinInfo) {
        this.name = name;
        this.column = column;
        this.javaType = javaType;
        this.joinInfo = joinInfo;
    }

    public String getName() {
        return name;
    }

    public String getColumn() {
        return column;
    }

    public JavaType getJavaType() {
        return javaType;
    }

    public JoinInfo getJoinInfo() {
        return joinInfo;
    }
}
