package actions;

import window.ImportSelectionDIalog;

import java.awt.event.ActionEvent;

public class ImportAction extends MyAbstractAction {
    public ImportAction() {
        putValue(SMALL_ICON, loadIcon("import.png"));
        putValue(NAME, "Bulk import");
        putValue(SHORT_DESCRIPTION, "Import data from CSV file to database");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        new ImportSelectionDIalog().setVisible(true);
    }
}
