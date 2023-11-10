package resource.implementation;

import resource.DBNode;
import resource.enums.ConstraintType;

public class AttributeConstraint extends DBNode {
    private ConstraintType constraintType;

    public AttributeConstraint(String name, DBNode parent, ConstraintType constraintType) {
        super(name, parent);
        this.constraintType = constraintType;
    }
}
