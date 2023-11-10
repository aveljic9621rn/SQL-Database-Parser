package window;

import components.Menu;
import components.*;
import main.AppCore;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Objects;

public class MainFrame extends JFrame {
    private static MainFrame instance;
    private MyTextField textField;

    private MainFrame() {
    }

    private void initializeGUI() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        setSize(screenWidth / 2, screenHeight / 2);
        ImageIcon img = new ImageIcon(Objects.requireNonNull(MainFrame.class.getResource("/images/database-icon.png")));
        setIconImage(img.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("RuBaz");

        Menu menu = new Menu();
        setJMenuBar(menu);

        textField = new MyTextField();
        add(textField, BorderLayout.CENTER);

        ResultSetView resultSetView = new ResultSetView();
        MistakesView mistakesView = new MistakesView();
        ResultMistakeSplitPane resultMistakeSplitPane = new ResultMistakeSplitPane(resultSetView, mistakesView);
        add(resultMistakeSplitPane, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                AppCore.getInstance().disconnectDatabase();
                System.exit(0);
            }
        };
        addWindowListener(exitListener);
    }

    public static MainFrame getInstance() {
        if (instance == null) {
            instance = new MainFrame();
            instance.initializeGUI();
        }
        return instance;
    }

    public String getTextFieldInput() {
        try {
            return textField.getDocument().getText(0, textField.getDocument().getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }

    public MyTextField getTextField() {
        return textField;
    }
}
