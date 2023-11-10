package components;

import javax.swing.*;
import javax.swing.text.BadLocationException;

public class MyTextField extends JTextPane {
    private String typedText;

    public MyTextField() {
        super();
        setContentType("text/html");
        try {
            typedText = getDocument().getText(0, getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        render();
    }

    public String getTextValue() {
        try {
            return getDocument().getText(0, getDocument().getLength());
        } catch (BadLocationException e) {
            return null;
        }
    }

    public void render() {
        typedText = getText();
        setText(typedText);
    }
}
