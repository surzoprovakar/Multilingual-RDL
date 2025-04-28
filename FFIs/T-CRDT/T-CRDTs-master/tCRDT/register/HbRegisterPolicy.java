package tCRDT.register;

import generic.concurrency.Policy;

public class HbRegisterPolicy implements Policy<RegisterOperation> {

    public static final String NAME = "hbReg";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(RegisterOperation op, RegisterOperation otherOp) {
        return true;
    }

}
