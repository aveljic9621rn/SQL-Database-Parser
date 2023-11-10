package utils.Importer;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MySQLImporter implements Importer {

    @Override
    public String generateQueryFromCSVIntoTable(File file, String tableName) {
        List<String[]> result = new ArrayList<>();
        Scanner scanner;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException ex) {
            return null;//neposotjeci fajl
        }
        if (!scanner.hasNextLine()) return null;//Prazan fajl
        String input = scanner.nextLine();
        String[] values = input.split(",", -1);
        String[] columns = values;
        if (columns.length == 0) return null;//Nema kolona

        while (scanner.hasNextLine()) {
            input = scanner.nextLine();
            values = input.split(",", -1);
            if (values.length != columns.length) continue;
            result.add(values);
        }

        StringBuilder query = new StringBuilder("INSERT INTO " + tableName + " (");
        for (int i = 0; i < columns.length; i++) {
            query.append("" + columns[i] + "");
            if (i != columns.length - 1) query.append(",");
        }
        query.append(") VALUES ");
        for (String[] row : result) {
            query.append("(");
            for (String val : row) {
                try {
                    Integer.parseInt(val);
                    Float.parseFloat(val);
                    Double.parseDouble(val);
                    Long.parseLong(val);
                    new BigInteger(val);
                    query.append((val.length() == 0 ? "NULL" : (val)) + ",");
                } catch (NumberFormatException exception) {
                    query.append((val.length() == 0 ? "NULL" : ("'" + val + "'")) + ",");
                }
            }
            query.deleteCharAt(query.length() - 1);
            query.append("),");
        }
        query.deleteCharAt(query.length() - 1);
        return query.toString();
    }
}
