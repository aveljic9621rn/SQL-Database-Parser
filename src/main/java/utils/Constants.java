package utils;

public class Constants {
    public static final String MYSQL_IP = "164.92.145.191";
    public static final String MYSQL_DATABASE = "bp_tim7";
    public static final String MYSQL_USERNAME = "bp_tim7";
    public static final String MYSQL_PASSWORD = "qRjMjduQ";

    public static final String[] KEYWORDS = {"VALUES", "AND", "ANY", "AS", "ASC", "AVG", "BETWEEN", "COUNT", "DELETE", "DESC", "DISTINCT", "EXEC", "FROM", "FULL", "FULL", "GROUP BY", "OUTER", "HAVING", "IN", "INNER", "INSERT", "INTO", "IS", "JOIN", "LEFT JOIN", "LIKE", "MAX", "MIN", "NOT", "NULL", "ON", "OR", "ORDER BY", "RIGHT", "SELECT", "SUM", "UPDATE", "WHERE", "HAVING"};
    public static final String[][] KEYWORD_GROUPS = {{"SELECT"}, {"FULL", "INNER", "LEFT", "RIGHT", "JOIN"}, {"ON"}, {"FROM"}, {"WHERE"}};
    public static final String[] JOIN_TERMINATOR = {"RIGHT", "LEFT", "JOIN", "OUTER", "iNNER", "NATURAL"};
    public static final String[] AGGREGATION_FUNC = {"AVG", "SUM", "MIN", "MAX", "COUNT"};
}
