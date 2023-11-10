package utils.Exporter;

import resource.data.Row;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class MySQLExporter implements Exporter {
    @Override
    public int exportRowsIntoCSV(List<Row> rows, File file) {
        try {
            FileWriter myWriter = new FileWriter(file);
            Set<String> header = null;
            StringBuilder table = new StringBuilder("");
            for (Row row : rows) {
                if (header == null) {
                    header = row.getFieldNames();
                    for (String value : header) {
                        table.append(value).append(",");
                    }
                    table.deleteCharAt(table.length() - 1);
                    table.append("\n");
                }
                for (Object value : row.getValues()) {
                    table.append(value).append(",");
                }
                table.deleteCharAt(table.length() - 1);
                table.append("\n");
            }
            myWriter.write(table.toString());
            myWriter.close();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 1;
        }
    }
}
