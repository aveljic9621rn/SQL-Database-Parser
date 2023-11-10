package resource.implementation;

import resource.DBNode;
import resource.DBNodeComposite;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InformationResource extends DBNodeComposite {
    public InformationResource(String name) {
        super(name, null);
    }

    public void postInit() {
        Map<String, Attribute> attributeMap = new HashMap<>();
        for (DBNode dbNode : getChildren()) {
            if (!(dbNode instanceof Entity entity)) continue;
            for (DBNode dbNode1 : entity.getChildren()) {
                if (!(dbNode1 instanceof Attribute attribute)) continue;
                attributeMap.put(entity.getName().toLowerCase(Locale.ROOT).trim() + "." + attribute.getName().toLowerCase(Locale.ROOT).trim(), attribute);
            }
        }

        for (DBNode dbNode : getChildren()) {
            if (!(dbNode instanceof Entity entity)) continue;
            for (DBNode dbNode1 : entity.getChildren()) {
                if (!(dbNode1 instanceof Attribute attribute)) continue;
                attribute.setInRelationWith(attributeMap.get(attribute.getRelationString()));
            }
        }
    }

    @Override
    public void addChild(DBNode child) {
        if (child instanceof Entity entity) {
            this.getChildren().add(entity);
        }
    }
}
