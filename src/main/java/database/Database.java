package database;

import resource.DBNode;
import resource.data.Row;
import resource.implementation.Attribute;
import resource.implementation.Entity;

import java.sql.ResultSet;
import java.util.List;

public interface Database {

    DBNode loadResource();

    List<Row> readDataFromTable(String tableName);

    List<Entity> getTableList();

    List<Attribute> getColumnsInTable(String table);

    ResultSet sendQuery(String query);

    void connect();

    void disconnect();
}
