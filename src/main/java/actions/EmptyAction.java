package actions;

import java.awt.event.ActionEvent;

public class EmptyAction extends MyAbstractAction {
    public EmptyAction() {
        putValue(SMALL_ICON, loadIcon("export.png"));
        putValue(NAME, "Export");
        putValue(SHORT_DESCRIPTION, "Export result set to a CSV file");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
    }
}
