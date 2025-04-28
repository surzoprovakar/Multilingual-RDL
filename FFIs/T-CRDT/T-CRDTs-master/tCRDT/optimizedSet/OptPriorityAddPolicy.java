package tCRDT.optimizedSet;

import generic.concurrency.Policy;
import tCRDT.set.LwwSetPolicy;
import tCRDT.set.SetOperation;

public class OptPriorityAddPolicy implements Policy<SetOperation> {

    public static final String NAME = "pAdd";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(SetOperation op, SetOperation otherOp) {
        return op.getType() == SetOperation.ADD &&
                otherOp.getType() == SetOperation.REMOVE && !otherOp.getSelfPolicyName().equals(LwwSetPolicy.NAME);
    }

}
