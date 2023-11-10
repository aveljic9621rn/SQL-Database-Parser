package database;

import resource.DBNode;
import resource.data.Row;
import resource.implementation.Attribute;
import resource.implementation.Entity;

import java.sql.ResultSet;
import java.util.List;

public interface Repository {
    DBNode getSchema();

    List<Entity> getTableList();

    List<Attribute> getColumnsInTable(String table);

    List<Row> get(String from);

    ResultSet sendQuery(String query);

    void connect();

    void disconnect();
}
