package actions;

public class ActionManager {
    private final ExportAction exportAction;
    private final ImportAction importAction;
    private final PrettyAction prettyAction;
    private final RunAction runAction;

    public ActionManager() {
        exportAction = new ExportAction();
        importAction = new ImportAction();
        prettyAction = new PrettyAction();
        runAction = new RunAction();
    }

    public ExportAction getExportAction() {
        return exportAction;
    }

    public ImportAction getImportAction() {
        return importAction;
    }

    public PrettyAction getPrettyAction() {
        return prettyAction;
    }

    public RunAction getRunAction() {
        return runAction;
    }
}
