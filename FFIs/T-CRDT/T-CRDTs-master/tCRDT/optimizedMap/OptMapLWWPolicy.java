package tCRDT.optimizedMap;

import generic.concurrency.Policy;
import tCRDT.map.MapMVPolicy;
import tCRDT.map.MapOperation;

public class OptMapLWWPolicy implements Policy<MapOperation> {

    public static final String NAME = "mLWW";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(MapOperation op, MapOperation otherOp) {
        return op.getSelfPolicyName().equals(NAME) && (otherOp.getSelfPolicyName().equals(MapMVPolicy.NAME) ||
                        (otherOp.getSelfPolicyName().equals(NAME) && op.getClock().compareTo(otherOp.getClock()) > 0));
    }

}
