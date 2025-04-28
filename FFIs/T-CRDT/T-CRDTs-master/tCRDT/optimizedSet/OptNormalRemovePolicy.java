package tCRDT.optimizedSet;

import generic.concurrency.Policy;
import tCRDT.set.SetOperation;

public class OptNormalRemovePolicy implements Policy<SetOperation> {

    public static final String NAME = "nRem";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(SetOperation setOperation, SetOperation setOperation2) {
        return false;
    }

}
