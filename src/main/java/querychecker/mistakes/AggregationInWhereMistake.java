package querychecker.mistakes;

import observer.notifications.MistakeNotification;
import queryparser.StatementParser;
import queryparser.statementimplementation.Select;
import queryparser.statementinterface.Statement;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class AggregationInWhereMistake extends Mistake {
    @Override
    public List<MistakeNotification> check(String query) {
        List<MistakeNotification> list = new ArrayList<>();
        StatementParser parserManager = new StatementParser();
        Statement statement = parserManager.parseQuery(query);
        if (statement == null) {
            return list;
        }
        if (statement instanceof Select select && select.getWhereExpression() != null) {
            String where = select.getWhereExpression().toString();
            for (String word : Constants.AGGREGATION_FUNC) {
                if (where.toUpperCase().contains(word + "(")) {
                    list.add(new MistakeNotification(getName(),
                            String.format(getDescription(), "Postoji rec za agregaciju u WHERE delu upita: " + word),
                            String.format(getRecommendation(), "Izbacite je pre nego sto pokrenete upit")));
                }
            }
        }

        return list;
    }

    public AggregationInWhereMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }
}
