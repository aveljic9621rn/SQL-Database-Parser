package querychecker.mistakes;

import main.AppCore;
import observer.notifications.MistakeNotification;
import queryparser.StatementParser;
import queryparser.statementimplementation.Select;
import queryparser.statementimplementation.Table;
import queryparser.statementinterface.Expression;
import queryparser.statementinterface.Statement;
import resource.DBNode;
import resource.implementation.Attribute;
import resource.implementation.Entity;
import resource.implementation.InformationResource;
import utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class TableOrColumnDoesNotExistMistake extends Mistake {
    private InformationResource informationResource;
    private List<Entity> tableList;
    private List<Attribute> allColumnsList;

    @Override
    public List<MistakeNotification> check(String query) {
        List<MistakeNotification> list = new ArrayList<>();
        informationResource = (InformationResource) AppCore.getInstance().getDatabase().loadResource();
        tableList = new ArrayList<>();
        allColumnsList = new ArrayList<>();
        for (DBNode dbNode1 : informationResource.getChildren()) {
            if (!(dbNode1 instanceof Entity entity)) continue;
            tableList.add(entity);
            for (DBNode dbNode2 : entity.getChildren()) {
                if (!(dbNode2 instanceof Attribute attribute)) continue;
                allColumnsList.add(attribute);
            }
        }

        StatementParser parserManager = new StatementParser();
        Statement statement = parserManager.parseQuery(query);
        if (statement == null) {
            return list;
        }
        if (statement instanceof Select select) {
            list.addAll(handleSelect(select));
        }

        return list;
    }

    private List<MistakeNotification> handleSelect(Select select) {
        ArrayList<String> selectItems = new ArrayList<>();
        for (Expression item : select.getSelectItems()) {
            if (item.toString().contains(" AS ")) {
                selectItems.add(item.toString().substring(0, item.toString().indexOf(" AS ")));
                continue;
            }
            if (item.toString().contains(" ")) {
                continue;
            }
            selectItems.add(item.toString());
        }
        ArrayList<MistakeNotification> list = new ArrayList<>(checkSelectParams(selectItems));
        Expression tableExpression = select.getFromTable();
        if (tableExpression != null) {
            MistakeNotification result = checkIfTableExists(tableExpression.toString());
            if (result != null) {
                list.add(result);
            }
        }

        String joins = select.getJoins();
        if (joins != null) {
            list.addAll(checkJoins(joins));
        }

        if (select.getWhereExpression() != null) {
            String where = select.getWhereExpression().toString();
        }

        return list;
    }

    private List<MistakeNotification> checkSelectParams(ArrayList<String> items) {
        ArrayList<MistakeNotification> list = new ArrayList<>();
        for (String column : items) {
            if (column.contains("AS")) {
                column = column.split("AS")[0];
            }

            if (column.contains("(")) {
                column = column.substring(column.indexOf("(") + 1, column.lastIndexOf(")"));
                String[] args = column.split("\\s*,\\s*");
                for (String s : args) {
                    MistakeNotification m = checkIfColumnExists(s);
                    if (m != null) list.add(m);
                }
                continue;
            }
            MistakeNotification result = checkIfColumnExists(column);
            if (result == null) {
                continue;
            }

            list.add(result);
        }
        return list;
    }

    private boolean isValidName(String s) {
        return !(s.contains(".") || s.contains(",") || s.contains(" ") || s.contains("(") || s.contains(")") || s.contains(">") || s.contains("<") || s.contains("="));
    }

    private List<MistakeNotification> checkJoins(String joins) {
        ArrayList<MistakeNotification> list = new ArrayList<>();
        String[] split = joins.split("\\s");
        for (int i = 0; i < split.length; i++) {
            if (split[i].equalsIgnoreCase("JOIN")) {
                MistakeNotification result = checkIfTableExists(split[i + 1]);
                if (result != null) {
                    list.add(result);
                }
            } else if (split[i].equalsIgnoreCase("USING")) {
                if (split[i + 1].contains("(")) {
                    split[i + 1] = split[i + 1].substring(split[i + 1].indexOf("(") + 1, split[i + 1].lastIndexOf(")"));
                }

                MistakeNotification result = checkIfColumnExists(split[i + 1]);
                if (result != null) {
                    list.add(result);
                }
            } else if (split[i].equalsIgnoreCase("ON")) {
                /*String joinColumns = getColumnsFromJoin(split, i + 1);
                String[] joinSplit = joinColumns.split("=");
                joinSplit[0] = joinSplit[0].substring(1);
                joinSplit[1] = joinSplit[1].substring(0, joinSplit[1].length() - 1);
                list.add(checkIfColumnExists(joinSplit[0]));
                list.add(checkIfColumnExists(joinSplit[1]));*/
            }
        }
        return list;
    }

    private String getColumnsFromJoin(String[] split, int index) {
        StringBuilder cur = new StringBuilder();
        for (int i = index; i < split.length; i++) {
            for (String word : Constants.JOIN_TERMINATOR) {
                if (split[i].equalsIgnoreCase(word)) {
                    return cur.toString();
                }
            }
            cur.append(split[i]);
        }

        return cur.toString();
    }

    private List<MistakeNotification> checkWhere(String joins) {
        ArrayList<MistakeNotification> list = new ArrayList<>();
        return list;
    }


    private MistakeNotification checkIfColumnExists(String parameter) {
        if (!isValidName(parameter)) return null;
        if (parameter.equals("*")) {
            return null;
        }

        boolean found = allColumnsList.stream().anyMatch(e -> e.getName().equalsIgnoreCase(parameter));
        /*List<Entity> tableList;//Ovo
        for(Attribute column:allColumnsList){
            if (column.getName().equalsIgnoreCase(parameter)) {
                found = true;
                break;
            }
        }*/

        /*for (Entity item : tableList) {
            if (found) {
                break;
            }

            List<Attribute> columnList;// = AppCore.getInstance().getDatabase().getColumnsInTable(item.getName());

            for (Attribute column : columnList) {
                if (column.getName().equalsIgnoreCase(parameter)) {
                    found = true;
                    break;
                }
            }
        }*/

        if (found) {
            return null;
        }

        return new MistakeNotification(getName(),
                String.format(getDescription(), "Kolona " + parameter),
                String.format(getRecommendation(), "Proverite da li ste dobro ispisali ime tabele"));
    }

    private MistakeNotification checkIfTableExists(String table) {
        if (!isValidName(table)) return null;
        boolean found = false;
        List<Entity> tableList = getTableList();
        for (Entity item : tableList) {
            if (item.getName().equalsIgnoreCase(table)) {
                found = true;
                break;
            }
        }

        if (found) {
            return null;
        }

        return new MistakeNotification(getName(),
                String.format(getDescription(), "Tabela " + table),
                String.format(getRecommendation(), "Proverite da li ste dobro ispisali ime tabele"));
    }

    public TableOrColumnDoesNotExistMistake(String name, String description, String recommendation) {
        super(name, description, recommendation);
    }

    public List<Entity> getTableList() {
        return tableList;
    }
}
