package database;

import resource.DBNode;
import resource.data.Row;
import resource.implementation.Attribute;
import resource.implementation.Entity;

import java.sql.ResultSet;
import java.util.List;

public class DatabaseImplementation implements Database {
    private final Repository repository;

    public DatabaseImplementation(Repository repository) {
        this.repository = repository;
    }

    @Override
    public DBNode loadResource() {
        return repository.getSchema();
    }

    @Override
    public List<Row> readDataFromTable(String tableName) {
        return repository.get(tableName);
    }

    @Override
    public List<Entity> getTableList() {
        return repository.getTableList();
    }

    @Override
    public List<Attribute> getColumnsInTable(String table) {
        return repository.getColumnsInTable(table);
    }

    @Override
    public ResultSet sendQuery(String query) {
        return repository.sendQuery(query);
    }

    @Override
    public void connect() {
        repository.connect();
    }

    @Override
    public void disconnect() {
        repository.disconnect();
    }
}
