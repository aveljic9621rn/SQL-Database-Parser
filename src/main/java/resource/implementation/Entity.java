package resource.implementation;

import resource.DBNode;
import resource.DBNodeComposite;

public class Entity extends DBNodeComposite {

    public Entity(String name, DBNode parent) {
        super(name, parent);
    }

    @Override
    public void addChild(DBNode child) {
        if (child instanceof Attribute attribute) {
            this.getChildren().add(attribute);
        }
    }
}
