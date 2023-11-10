package utils.Exporter;

import resource.data.Row;

import java.io.File;
import java.util.List;

public interface Exporter {
    int exportRowsIntoCSV(List<Row> rows, File file);
}
