package resource.data;

import java.util.*;

public class Row {
    private String name;
    private Map<String, Object> fields;

    public Row() {
        this.fields = new HashMap<>();
    }

    public Set<String> getFieldNames() {
        return fields.keySet();
    }

    public Collection<Object> getValues() {
        return fields.values();
    }

    public void addField(String fieldName, Object value) {
        this.fields.put(fieldName, value);
    }

    public void removeField(String fieldName) {
        this.fields.remove(fieldName);
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getField(String name) {
        return fields.get(name);
    }
}
