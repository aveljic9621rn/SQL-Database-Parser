package queryparser.statementimplementation;

import queryparser.Pair;
import queryparser.Util;
import queryparser.statementinterface.Expression;
import queryparser.statementinterface.Statement;
import queryparser.visitorInterface.StatementVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CreateProcedure implements Statement {
    private String query;
    private String procedureName;
    private List<Parameter> parameters;
    private String procedureBody;
    private List<String> procedureLines;


    public CreateProcedure(String query) {
        parameters = null;
        query = query.trim();
        query = query.replaceAll("\\s+", " ");
        this.query = query;
        if (query.toUpperCase(Locale.ROOT).startsWith("CREATE PROCEDURE")) query = query.substring(16).trim();

        Pair<String, String> procedureNameResult = Util.readWordOrQuotes(query, new Character[]{' ', ',', '('}, new Character[]{});
        procedureName = procedureNameResult.getFirst().trim();
        if (procedureName.length() == 0) {
            return;//Procedura mora da ima ime
        }
        query = procedureNameResult.getSecond().trim();

        if (query.charAt(0) == '(') {
            int zag = 1;
            int i = 0;
            query = query.substring(1).trim();

            while (zag > 0) {
                if (i >= query.length()) return;//Zagrada se ne zatvara
                if (query.charAt(i) == '(')
                    zag++;
                if (query.charAt(i) == ')')
                    zag--;
                i++;
            }
            String untilCb = query.substring(0, i).trim();
            untilCb = untilCb.substring(0, untilCb.length() - 1).trim();
            String[] vars = untilCb.split("\\s*,\\s*");
            for (String s : vars) {
                if (parameters == null) parameters = new ArrayList();
                String[] split = s.split("\\s+");
                String name = split[1];
                boolean output = split[0].equalsIgnoreCase("OUT");
                String type = "";
                for (int i1 = 2; i1 < split.length; i1++) {
                    type += split[i1] + " ";
                }
                type = type.substring(0, type.length() - 1);
                Parameter v = new Parameter(name, output, type);
                parameters.add(v);
            }
            query = query.substring(i).trim();
        }
        if (query.toUpperCase(Locale.ROOT).startsWith("AS")) query = query.substring(2).trim();

        query = query.substring(query.toUpperCase(Locale.ROOT).indexOf("BEGIN")).trim().substring(5).trim();
        procedureBody = query.substring(0, query.toUpperCase(Locale.ROOT).indexOf("END")).trim();
        procedureLines = Arrays.asList(procedureBody.split("\\s*;\\s*"));
        if (procedureLines.size() == 0) procedureLines = null;
        if (procedureBody.length() == 0) procedureBody = null;

    }

    public List<String> getUnusedVariablesList() {
        List<String> list = new ArrayList<>();
        List<Pair<Integer, Variable>> vars = new ArrayList<>();
        for (String line : procedureLines) {
            String newLine = line.trim();
            if (!newLine.toUpperCase(Locale.ROOT).startsWith("DECLARE")) continue;
            newLine = newLine.substring(7).trim();
            String[] words = newLine.split("\\s+");
            String name = words[0].trim();
            String type = words[1].trim();
            Variable variable = new Variable(name, type);
            vars.add(new Pair<>(procedureLines.indexOf(line), variable));
        }

        for (Pair<Integer, Variable> variablePair : vars) {
            int defRow = variablePair.getFirst();
            Variable variable = variablePair.getSecond();
            boolean found = false;
            for (int i = defRow + 1; i < procedureLines.size(); i++) {
                String line = procedureLines.get(i);
                if (line.contains(variable.getName())) {
                    found = true;
                }
            }
            if (!found) {
                list.add(variable.getName());
            }
        }
        return list;
    }

    @Override
    public void accept(StatementVisitor statementVisitor) {

    }

    public String getQuery() {
        return query;
    }

}
