package components;

import window.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ResultMistakeSplitPane extends JSplitPane {
    ResultSetView resultSetView;
    MistakesView mistakesView;

    public ResultMistakeSplitPane(ResultSetView resultSetView, MistakesView mistakesView) {
        super(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(resultSetView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), new JScrollPane(mistakesView, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        ResultMistakeSplitPane self = this;
        this.resultSetView = resultSetView;
        this.mistakesView = mistakesView;
        MainFrame.getInstance().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                self.setPreferredSize(new Dimension(0, MainFrame.getInstance().getContentPane().getHeight() / 2));
                self.setDividerLocation(self.getWidth() * 2 / 3);
                MainFrame.getInstance().repaint();
                MainFrame.getInstance().revalidate();
            }
        });
    }
}
