package resource;

public abstract class DBNode {
    private String name;
    private DBNode parent;

    public DBNode(String name, DBNode parent) {
        this.name = name;
        this.parent = parent;
    }

    public DBNode() {
        this.name = null;
        this.parent = null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DBNode otherObj) {
            return this.getName().equals(otherObj.getName());
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public DBNode getParent() {
        return parent;
    }
}
