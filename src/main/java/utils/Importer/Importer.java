package utils.Importer;

import java.io.File;

public interface Importer {
    String generateQueryFromCSVIntoTable(File file, String tableName);
}
