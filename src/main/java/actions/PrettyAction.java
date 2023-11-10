package actions;

import utils.pretty.IPrettifier;
import utils.pretty.Prettifier;
import window.MainFrame;

import java.awt.event.ActionEvent;

public class PrettyAction extends MyAbstractAction {
    public PrettyAction() {
        putValue(SMALL_ICON, loadIcon("pretty.png"));
        putValue(NAME, "Pretty");
        putValue(SHORT_DESCRIPTION, "Make code prettier");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String query = MainFrame.getInstance().getTextField().getTextValue();
        IPrettifier prettifier = new Prettifier();

        MainFrame.getInstance().getTextField().setText(prettifier.prettify(query));
    }
}
