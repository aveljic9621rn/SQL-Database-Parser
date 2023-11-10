package components;

import main.AppCore;
import observer.ISubscriber;
import observer.notifications.MistakeNotification;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MistakesView extends JTextPane implements ISubscriber {
    List<MistakeNotification> mistakeNotifications;

    public MistakesView() {
        AppCore.getInstance().addSubscriber(this);
        mistakeNotifications = new ArrayList<>();
        setLayout(new BorderLayout());
        setContentType("text/html");
        setText("<html><p>No mistakes</p></html>");
        setEditable(false);
    }

    private void addMistakeView(MistakeNotification mistakeNotification) {
        mistakeNotifications.add(mistakeNotification);
        render();
    }

    private void render() {
        StringBuilder stringBuilder = new StringBuilder("<html>");
        for (MistakeNotification mistakeNotification : mistakeNotifications) {
            stringBuilder.append("<h2>" + mistakeNotification.getName() + "</h2> <p>" + mistakeNotification.getDescription() + "</p>" + "<p>" + mistakeNotification.getRecommendation() + "</p>");
        }
        setText(stringBuilder.toString());
        stringBuilder.append("</html>");

    }

    private void clearMistakeViews() {
        mistakeNotifications = new ArrayList<>();
        render();
    }

    @Override
    public void update(Object notification) {
        if (notification instanceof ArrayList list) {
            if (list.size() == 0) {
                setText("<html><p>No mistakes</p></html>");
                return;
            }
            clearMistakeViews();
            for (Object o : list) {
                if (!(o instanceof MistakeNotification mistakeNotification)) continue;
                addMistakeView(mistakeNotification);
            }
            render();
        }
    }
}
