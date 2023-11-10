package actions;

import javax.swing.*;

public abstract class MyAbstractAction extends AbstractAction {
    public Icon loadIcon(String filename) {
        return new ImageIcon(MyAbstractAction.class.getResource("/images/actions/" + filename));
    }
}
