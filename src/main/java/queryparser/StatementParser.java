package queryparser;

import queryparser.statementimplementation.CreateProcedure;
import queryparser.statementimplementation.Insert;
import queryparser.statementimplementation.Select;
import queryparser.statementinterface.Statement;

import java.util.Locale;

public class StatementParser {
    public Statement parseQuery(String query) {
        String clean = query.trim().toUpperCase(Locale.ROOT);
        if (clean.startsWith("INSERT INTO"))
            return new Insert(query);
        if (clean.startsWith("SELECT"))
            return new Select(query);
        if (clean.startsWith("CREATE PROCEDURE") || clean.startsWith("CREATE OR REPLACE PROCEDURE"))
            return new CreateProcedure(query);

        return null;
    }
}
