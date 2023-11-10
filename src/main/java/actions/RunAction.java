package actions;

import main.AppCore;
import window.MainFrame;

import java.awt.event.ActionEvent;

public class RunAction extends MyAbstractAction {
    public RunAction() {
        putValue(SMALL_ICON, loadIcon("run.png"));
        putValue(NAME, "Run");
        putValue(SHORT_DESCRIPTION, "Run code");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        AppCore.getInstance().run(MainFrame.getInstance().getTextFieldInput());
    }
}
