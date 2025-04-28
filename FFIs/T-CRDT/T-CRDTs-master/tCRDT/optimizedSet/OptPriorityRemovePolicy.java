package tCRDT.optimizedSet;

import generic.concurrency.Policy;
import tCRDT.set.NormalAddPolicy;
import tCRDT.set.SetOperation;

public class OptPriorityRemovePolicy implements Policy<SetOperation> {

    public static final String NAME = "pRem";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(SetOperation op, SetOperation otherOp) {
        return op.getType() == SetOperation.REMOVE &&
                otherOp.getType() == SetOperation.ADD && otherOp.getSelfPolicyName().equals(NormalAddPolicy.NAME);
    }
}
