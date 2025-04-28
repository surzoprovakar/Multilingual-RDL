package tCRDT.register;

import generic.concurrency.Policy;

public class WeakLWWPolicy implements Policy<RegisterOperation> {

    public static final String NAME = "wLWW";

    @Override
    public String getName() {
        return NAME;
    }

    private boolean isLWW(RegisterOperation op) {
        return op.getSelfPolicyName().equals(NAME) || op.getSelfPolicyName().equals(StrongLWWPolicy.NAME);
    }

    @Override
    public Boolean apply(RegisterOperation op, RegisterOperation otherOp) {
        return isLWW(op) && isLWW(otherOp) && op.getClock().compareTo(otherOp.getClock()) > 0;
    }
}
