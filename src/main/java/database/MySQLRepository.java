package database;

import database.settings.Settings;
import main.AppCore;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import resource.DBNode;
import resource.data.Row;
import resource.enums.AttributeType;
import resource.implementation.Attribute;
import resource.implementation.Entity;
import resource.implementation.InformationResource;

import java.io.StringReader;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class MySQLRepository implements Repository {
    private final Settings settings;
    private Connection connection;

    public MySQLRepository(Settings settings) {
        this.settings = settings;
        connect();
    }

    private void initConnection() throws SQLException {
        String ip = (String) settings.getParameter("mysql_ip");
        String database = (String) settings.getParameter("mysql_database");
        String username = (String) settings.getParameter("mysql_username");
        String password = (String) settings.getParameter("mysql_password");
        //Class.forName("net.sourceforge.jtds.jdbc.Driver");
        connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + database, username, password);
    }

    @Override
    public List<Entity> getTableList() {
        DBNode root = getSchema();
        if (!(root instanceof InformationResource informationResource)) return null;
        return informationResource.getChildren().stream()
                .filter(e -> e instanceof Entity)
                .map(e -> (Entity) e)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attribute> getColumnsInTable(String tableName) {
        DBNode root = getSchema();
        if (!(root instanceof InformationResource informationResource)) return null;

        List<DBNode> tables = informationResource.getChildren().stream()
                .filter(e -> e.getName().equalsIgnoreCase(tableName))
                .toList();
        if (tables.size() == 0) return null;//tabela ne postoji
        DBNode tableDBNode = tables.get(0);
        if (!(tableDBNode instanceof Entity entity)) return null;//Nece da se desi


        return entity.getChildren().stream()
                .filter(e -> e instanceof Attribute)
                .map(e -> (Attribute) e)
                .collect(Collectors.toList());
    }

    private void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            connection = null;
        }
    }

    @Override
    public ResultSet sendQuery(String query) {
        if (query.trim().toUpperCase(Locale.ROOT).startsWith("SELECT")) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                return preparedStatement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.execute();
            return null;//Provera uspeha inserta
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        /*
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Statement statement;
        try {
            statement = parserManager.parse(new StringReader(query));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (statement instanceof Select) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                return preparedStatement.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        } else if (statement instanceof Insert) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.execute();
                return null;//Provera uspeha inserta
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }*/
    }

    @Override
    public void connect() {
        try {
            initConnection();
        } catch (Exception e) {
            System.out.println("Neuspesno povezivanje sa bazom");
        }
    }

    @Override
    public void disconnect() {
        closeConnection();
    }

    @Override
    public DBNode getSchema() {
        try {
            //this.initConnection();

            DatabaseMetaData metaData = connection.getMetaData();
            InformationResource ir = new InformationResource("RAF_BP_Primer");

            String[] tableType = {"TABLE"};
            ResultSet tables = metaData.getTables(connection.getCatalog(), null, null, tableType);


            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                ResultSet rsImportedKeys = metaData.getImportedKeys(connection.getCatalog(), null, tableName);
                Map<String, String> foreignKeys = new HashMap<>();
                while (rsImportedKeys.next()) {
                    String fkColumnName = rsImportedKeys.getString("FKCOLUMN_NAME");
                    String pkTableName = rsImportedKeys.getString("PKTABLE_NAME");
                    String pkColumnName = rsImportedKeys.getString("PKCOLUMN_NAME");
                    foreignKeys.put(fkColumnName.toLowerCase(Locale.ROOT).trim(), pkTableName.toLowerCase(Locale.ROOT).trim() + "." + pkColumnName.toLowerCase(Locale.ROOT).trim());
                }

                if (tableName.contains("trace")) continue;
                Entity newTable = new Entity(tableName, ir);
                ir.addChild(newTable);

                ResultSet columns = metaData.getColumns(connection.getCatalog(), null, tableName, null);
                while (columns.next()) {
                    // COLUMN_NAME TYPE_NAME COLUMN_SIZE ....

                    String columnName = columns.getString("COLUMN_NAME");
                    String refer = foreignKeys.get(columnName);
                    String columnType = columns.getString("TYPE_NAME");
                    boolean isNullable = columns.getBoolean("IS_NULLABLE");
                    boolean isAutoIncrement = columns.getBoolean("IS_AUTOINCREMENT");

                    int columnSize = Integer.parseInt(columns.getString("COLUMN_SIZE"));

//                    ResultSet pkeys = metaData.getPrimaryKeys(connection.getCatalog(), null, tableName);
//
//                    while (pkeys.next()){
//                        String pkColumnName = pkeys.getString("COLUMN_NAME");
//                    }

                    Attribute attribute = new Attribute(columnName, newTable,
                            AttributeType.valueOf(
                                    String.join("_", columnType.toUpperCase().split(" "))),
                            columnSize, isNullable, isAutoIncrement, refer);
                    newTable.addChild(attribute);
                }
            }

            //TODO Ogranicenja nad kolonama? Relacije?
            ir.postInit();
            AppCore.getInstance().updateInformationResource(ir);
            return ir;
            //String isNullable = columns.getString("IS_NULLABLE");
            // ResultSet foreignKeys = metaData.getImportedKeys(connection.getCatalog(), null, table.getName());
            // ResultSet primaryKeys = metaData.getPrimaryKeys(connection.getCatalog(), null, table.getName());

        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            //this.closeConnection();
        }

        return null;
    }

    @Override
    public List<Row> get(String from) {
        List<Row> rows = new ArrayList<>();

        try {
            //this.initConnection();

            String query = "SELECT * FROM " + from;
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            ResultSetMetaData resultSetMetaData = rs.getMetaData();

            while (rs.next()) {

                Row row = new Row();
                row.setName(from);

                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    row.addField(resultSetMetaData.getColumnName(i), rs.getString(i));
                }
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //this.closeConnection();
        }

        return rows;
    }
}
