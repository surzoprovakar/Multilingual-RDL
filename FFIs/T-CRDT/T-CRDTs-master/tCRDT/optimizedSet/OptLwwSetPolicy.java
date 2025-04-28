package tCRDT.optimizedSet;

import generic.concurrency.Policy;
import tCRDT.set.SetOperation;

public class OptLwwSetPolicy implements Policy<SetOperation> {

    public static final String NAME = "lww";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(SetOperation op, SetOperation otherOp) {
        return op.getSelfPolicyName().equals(NAME) &&
                (!otherOp.getSelfPolicyName().equals(NAME) || op.getClock().compareTo(otherOp.getClock()) > 0);
    }
}
