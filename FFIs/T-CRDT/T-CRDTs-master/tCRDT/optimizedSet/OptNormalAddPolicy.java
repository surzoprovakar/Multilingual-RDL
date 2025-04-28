package tCRDT.optimizedSet;

import generic.concurrency.Policy;
import tCRDT.set.NormalRemovePolicy;
import tCRDT.set.SetOperation;

public class OptNormalAddPolicy implements Policy<SetOperation> {

    public static final String NAME = "nAdd";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(SetOperation op, SetOperation otherOp) {
        return op.getType() == SetOperation.ADD &&
                otherOp.getType() == SetOperation.REMOVE && otherOp.getSelfPolicyName().equals(NormalRemovePolicy.NAME);
    }

}
