package tCRDT.optimizedMap;

import generic.concurrency.Policy;
import tCRDT.map.MapOperation;

public class OptHbMapPolicy implements Policy<MapOperation> {

    public static final String NAME = "hbMap";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(MapOperation op, MapOperation otherOp) {
        return true;
    }

}
