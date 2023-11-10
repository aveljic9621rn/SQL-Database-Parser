package queryparser.statementimplementation;

public class Column {
    String columnName;
    Table table;

    public Column(String columnName, Table table) {
        this.columnName = columnName;
        this.table = table;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getName() {
        return (table.getAlias() == null ? table.getName() : table.getAlias()) + "." + columnName;
    }

    public Table getTable() {
        return table;
    }
}
