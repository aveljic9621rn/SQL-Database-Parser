package resource.implementation;

import resource.DBNode;
import resource.DBNodeComposite;
import resource.enums.AttributeType;

public class Attribute extends DBNodeComposite {
    private AttributeType attributeType;
    private int length;
    private Attribute inRelationWith;
    private String relationString;
    private boolean nullable;
    private boolean autoIncrement;

    public Attribute(String name, DBNode parent) {
        super(name, parent);
    }

    public Attribute(String name, DBNode parent, AttributeType attributeType, int length, boolean nullable, boolean autoIncrement, String relationString) {
        super(name, parent);
        this.attributeType = attributeType;
        this.length = length;
        this.nullable = nullable;
        this.autoIncrement = autoIncrement;
        this.relationString = relationString;
    }

    @Override
    public void addChild(DBNode child) {
        if (child instanceof AttributeConstraint attributeConstraint) {
            this.getChildren().add(attributeConstraint);
        }
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public String getRelationString() {
        return relationString;
    }

    public Attribute getInRelationWith() {
        return inRelationWith;
    }


    public void setInRelationWith(Attribute inRelationWith) {
        this.inRelationWith = inRelationWith;
    }
}
