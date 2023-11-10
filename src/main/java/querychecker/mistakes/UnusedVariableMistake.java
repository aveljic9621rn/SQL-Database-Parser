package querychecker.mistakes;

import observer.notifications.MistakeNotification;
import queryparser.StatementParser;
import queryparser.statementimplementation.CreateProcedure;
import queryparser.statementinterface.Statement;

import java.util.ArrayList;
import java.util.List;

/*
CREATE PROCEDURE p7(IN naziv CHAR, OUT trosak INT)
BEGIN
DECLARE productCount INT DEFAULT 0;
select sum(salary) from employees;
END
*/

public class UnusedVariableMistake extends Mistake {
    @Override
    public List<MistakeNotification> check(String query) {
        List<MistakeNotification> list = new ArrayList<>();
        StatementParser statementParser = new StatementParser();
        Statement a = statementParser.parseQuery(query);
        if (!(a instanceof CreateProcedure createProcedure)) return list;

        for (String variable : createProcedure.getUnusedVariablesList()) {
            list.add(new MistakeNotification(getName(), String.format(getDescription(), variable), getRecommendation()));
        }

        return list;
    }

    public UnusedVariableMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }
}
