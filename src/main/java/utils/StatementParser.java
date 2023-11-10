package utils;

import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;

import java.io.StringReader;

public class StatementParser {

    public static Statement parseStatement(String query) {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        try {
            return parserManager.parse(new StringReader(query));
        } catch (Exception e) {
            return null;
        }
    }
}
