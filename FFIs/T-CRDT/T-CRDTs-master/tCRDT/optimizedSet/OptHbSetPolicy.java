package tCRDT.optimizedSet;

import generic.concurrency.Policy;
import tCRDT.set.SetOperation;

public class OptHbSetPolicy implements Policy<SetOperation> {

    public static final String NAME = "hbSet";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(SetOperation op, SetOperation otherOp) {
        return true;
    }

}
