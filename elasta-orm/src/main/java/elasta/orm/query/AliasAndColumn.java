package elasta.orm.query;

import java.util.Objects;

/**
 * Created by Jango on 17/02/11.
 */
public class AliasAndColumn {
    final String alias;
    final String column;

    public AliasAndColumn(String alias, String column) {
        Objects.requireNonNull(alias);
        Objects.requireNonNull(column);
        this.alias = alias;
        this.column = column;
    }

    public String getAlias() {
        return alias;
    }

    public String getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AliasAndColumn that = (AliasAndColumn) o;

        if (alias != null ? !alias.equals(that.alias) : that.alias != null) return false;
        return column != null ? column.equals(that.column) : that.column == null;

    }

    @Override
    public int hashCode() {
        int result = alias != null ? alias.hashCode() : 0;
        result = 31 * result + (column != null ? column.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AliasAndColumn{" +
            "alias='" + alias + '\'' +
            ", column='" + column + '\'' +
            '}';
    }

    public String toSql() {
        return alias + "." + column;
    }
}
