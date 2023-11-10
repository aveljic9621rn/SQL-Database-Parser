package components;

import main.AppCore;

import javax.swing.*;

public class Menu extends JMenuBar {
    public Menu() {
        super();

        JMenu mnuFile = new JMenu("File");
        mnuFile.add(AppCore.getInstance().getActionManager().getExportAction());
        mnuFile.add(AppCore.getInstance().getActionManager().getImportAction());
        add(mnuFile);

        JMenu mnuFormat = new JMenu("Format");
        mnuFormat.add(AppCore.getInstance().getActionManager().getPrettyAction());
        add(mnuFormat);

        JMenu mnuRun = new JMenu("Run");
        mnuRun.add(AppCore.getInstance().getActionManager().getRunAction());
        add(mnuRun);
    }
}
