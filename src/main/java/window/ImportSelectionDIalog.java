package window;

import main.AppCore;
import resource.implementation.Entity;
import utils.Importer.Importer;
import utils.Importer.MySQLImporter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ImportSelectionDIalog extends SimpleDialog {
    JFileChooser jFileChooser = new JFileChooser();

    public ImportSelectionDIalog() {
        super(null, "Select data", false);
        ImportSelectionDIalog self = this;

        List<Entity> tables = AppCore.getInstance().getDatabase().getTableList();
        if (tables == null) return;
        JComboBox tableComboBox = new JComboBox(tables.stream()
                .map(e -> e.getName())
                .toList()
                .toArray());

        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "CSV Table", "csv");
        jFileChooser.setFileFilter(filter);
        jFileChooser.setCurrentDirectory(new File
                (System.getProperty("user.home") + System.getProperty("file.separator") + "Desktop"));
        JButton button = new JButton("Choose file");
        AtomicReference<File> myFile = new AtomicReference<>();
        button.addActionListener(e -> {
            if (jFileChooser.showOpenDialog(self) != JFileChooser.APPROVE_OPTION) return;
            myFile.set(jFileChooser.getSelectedFile());
        });

        Button okButton = new Button("Ok");

        okButton.addActionListener(e -> {
            File file = myFile.get();
            if (file == null) return;
            Importer importer = new MySQLImporter();
            String query = importer.generateQueryFromCSVIntoTable(file, tableComboBox.getSelectedItem().toString());
            if (query == null) {
                return;
            }
            AppCore.getInstance().run(query);
            self.dispose();
        });

        setLayout(new BorderLayout());
        add(button, BorderLayout.WEST);
        add(tableComboBox, BorderLayout.EAST);
        add(okButton, BorderLayout.SOUTH);
    }

    @Override
    public void update(Object notification) {

    }
}
