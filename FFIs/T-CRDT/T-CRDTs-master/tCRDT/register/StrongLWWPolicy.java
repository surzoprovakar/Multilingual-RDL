package tCRDT.register;

import generic.concurrency.Policy;

public class StrongLWWPolicy implements Policy<RegisterOperation> {

    public static final String NAME = "sLWW";

    @Override
    public String getName() {
        return NAME;
    }

    private boolean isLWW(RegisterOperation op) {
        return op.getSelfPolicyName().equals(NAME) || op.getSelfPolicyName().equals(WeakLWWPolicy.NAME);
    }

    @Override
    public Boolean apply(RegisterOperation op, RegisterOperation otherOp) {
        return isLWW(op) && (otherOp.getSelfPolicyName().equals(MVPolicy.NAME) || op.getClock().compareTo(otherOp.getClock()) > 0);
    }

}
