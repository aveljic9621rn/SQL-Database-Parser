package components;

import main.AppCore;
import observer.ISubscriber;
import resource.data.Row;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class ResultSetView extends JTextPane implements ISubscriber {
    public ResultSetView() {
        AppCore.getInstance().addSubscriber(this);
        setLayout(new BorderLayout());
        setContentType("text/html");
        setEditable(false);
    }

    @Override
    public void update(Object notification) {
        if (!(notification instanceof List rows)) return;
        Set<String> header = null;
        StringBuilder table = new StringBuilder("<html><table border=1>");
        for (Object o : rows) {
            if (!(o instanceof Row row)) continue;
            if (header == null) {
                header = row.getFieldNames();
                table.append("<tr>");
                for (String value : header) {
                    table.append("<th>" + value + "</th>");
                }
                table.append("</tr>");
            }
            table.append("<tr>");
            for (Object value : row.getValues()) {
                table.append("<td>" + value + "</td>");
            }
            table.append("</tr>");
        }
        table.append("</table></html>");
        setText(table.toString());
    }
}
