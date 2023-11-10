package querychecker.mistakes;

import observer.notifications.MistakeNotification;
import queryparser.StatementParser;
import queryparser.statementimplementation.Select;
import queryparser.statementinterface.Expression;
import queryparser.statementinterface.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AliasNoQuotesMistake extends Mistake {
    @Override
    public List<MistakeNotification> check(String query) {
        List<MistakeNotification> list = new ArrayList<>();
        StatementParser parserManager = new StatementParser();
        Statement statement = parserManager.parseQuery(query);
        if (statement == null) {
            return list;
        }
        if (statement instanceof Select select) {
            ArrayList<String> selectItems = new ArrayList<>();
            for (Expression item : select.getSelectItems()) {
                selectItems.add(item.toString());
            }

            for (String item : selectItems) {
                String[] split = item.toLowerCase().split(" as ");
                if (split.length == 1) {
                    continue;
                }

                StringBuilder aliasBuilder = new StringBuilder();
                for (int i = 1; i < split.length; i++) {
                    aliasBuilder.append(split[i]).append(" ");
                }
                String alias = aliasBuilder.toString().trim();
                if (alias.contains(" ") && !alias.startsWith("\"") && !alias.endsWith("\"")) {
                    list.add(new MistakeNotification(getName(),
                            String.format(getDescription(), alias),
                            String.format(getRecommendation(), "Proverite da li svaki alijas koji ima razmak u sebi ima znake navoda oko sebe")));
                }
            }
        }

        return list;
    }

    public AliasNoQuotesMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }
}
