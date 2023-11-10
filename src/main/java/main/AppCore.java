package main;

import actions.ActionManager;
import database.Database;
import database.DatabaseImplementation;
import database.MySQLRepository;
import database.settings.Settings;
import database.settings.SettingsImplementation;
import observer.MyPublisher;
import observer.notifications.MistakeNotification;
import querychecker.MyQueryChecker;
import querychecker.QueryChecker;
import resource.DBNode;
import resource.data.Row;
import resource.implementation.Attribute;
import resource.implementation.Entity;
import resource.implementation.InformationResource;
import utils.Constants;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

public class AppCore extends MyPublisher {
    private static AppCore instance;
    private Database database;
    private ActionManager actionManager;
    private Settings settings;
    private QueryChecker queryChecker;
    private List<Row> currentResultSetRows;
    private InformationResource informationResource = null;

    private AppCore() {

    }

    public void disconnectDatabase() {
        database.disconnect();
    }

    private Settings initSettings() {
        Settings settingsImplementation = new SettingsImplementation();
        settingsImplementation.addParameter("mysql_ip", Constants.MYSQL_IP);
        settingsImplementation.addParameter("mysql_database", Constants.MYSQL_DATABASE);
        settingsImplementation.addParameter("mysql_username", Constants.MYSQL_USERNAME);
        settingsImplementation.addParameter("mysql_password", Constants.MYSQL_PASSWORD);
        return settingsImplementation;
    }

    private void init() {
        settings = initSettings();
        database = new DatabaseImplementation(new MySQLRepository(this.settings));
        DBNode schema = database.loadResource();
        if (!(schema instanceof InformationResource informationResource)) {
            return;
        }
        for (DBNode child : informationResource.getChildren()) {
            if (!(child instanceof Entity entity)) continue;
            for (DBNode grandchild : entity.getChildren()) {
                if (!(grandchild instanceof Attribute attribute)) continue;
                //Primer prolaza kroz semu
            }
        }
        queryChecker = new MyQueryChecker();
        actionManager = new ActionManager();
    }

    public static AppCore getInstance() {
        if (instance == null) {
            instance = new AppCore();
            instance.init();
        }
        return instance;
    }

    public InformationResource getLatestInformationResource() {
        return informationResource;
    }

    public void updateInformationResource(InformationResource informationResource) {
        this.informationResource = informationResource;
    }

    public QueryChecker getQueryChecker() {
        return queryChecker;
    }

    public Database getDatabase() {
        return database;
    }

    public ActionManager getActionManager() {
        return actionManager;
    }

    public List<Row> getCurrentResultSetRows() {
        return currentResultSetRows;
    }

    public void run(String query) {
        List<MistakeNotification> stack = queryChecker.checkQuery(query);
        notifySubscribers(stack);
        if (stack.size() > 0) return;
        ResultSet resultSet = database.sendQuery(query);

        List<Row> rows = new ArrayList<>();
        try {
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            while (resultSet.next()) {

                Row row = new Row();
                row.setName("row");

                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    row.addField(resultSetMetaData.getColumnName(i), resultSet.getString(i));
                }
                rows.add(row);
            }
        } catch (Exception e) {
            //Javlja se kod inserta
            return;
        }
        currentResultSetRows = rows;
        notifySubscribers(rows);
    }
}
