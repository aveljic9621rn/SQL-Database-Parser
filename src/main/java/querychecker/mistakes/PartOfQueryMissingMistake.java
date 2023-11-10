package querychecker.mistakes;

import observer.notifications.MistakeNotification;
import queryparser.StatementParser;
import queryparser.statementimplementation.Select;
import queryparser.statementinterface.Statement;

import java.util.ArrayList;
import java.util.List;

public class PartOfQueryMissingMistake extends Mistake {
    @Override
    public List<MistakeNotification> check(String query) {
        List<MistakeNotification> list = new ArrayList<>();
        StatementParser parserManager = new StatementParser();
        Statement statement = parserManager.parseQuery(query);
        if (statement == null) {
            return list;
        }
        if (statement instanceof Select) {
            int fromPos = query.toUpperCase().indexOf(" FROM ");
            if (fromPos == -1) {
                list.add(new MistakeNotification(getName(),
                        String.format(getDescription(), "FROM"),
                        String.format(getRecommendation(), "Dodajte rec FROM")));
            }
            int joinPos = query.toUpperCase().indexOf(" JOIN ");
            int usingPos = query.toUpperCase().indexOf(" USING ");
            int onPos = query.toUpperCase().indexOf(" ON ");
            if (fromPos == 1 && joinPos == -1 && (usingPos != -1 || onPos != 1)) {
                list.add(new MistakeNotification(getName(),
                        String.format(getDescription(), "JOIN"),
                        String.format(getRecommendation(), "Dodajte rec JOIN zbog USING ili ON")));
            }
        }

        return list;
    }

    public PartOfQueryMissingMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }
}
