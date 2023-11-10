package actions;

import main.AppCore;
import resource.data.Row;
import utils.Exporter.Exporter;
import utils.Exporter.MySQLExporter;
import window.MainFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class ExportAction extends MyAbstractAction {
    public ExportAction() {
        putValue(SMALL_ICON, loadIcon("export.png"));
        putValue(NAME, "Export");
        putValue(SHORT_DESCRIPTION, "Export result set to a CSV file");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser jFileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "CSV Table", "csv");
        jFileChooser.setFileFilter(filter);
        jFileChooser.setCurrentDirectory(new File
                (System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop"));
        if (jFileChooser.showSaveDialog(MainFrame.getInstance()) != JFileChooser.APPROVE_OPTION) return;
        File file = jFileChooser.getSelectedFile();


        List<Row> rows = AppCore.getInstance().getCurrentResultSetRows();

        Exporter exporter = new MySQLExporter();
        exporter.exportRowsIntoCSV(rows, file);
    }
}
