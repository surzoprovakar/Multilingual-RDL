package tCRDT.optimizedMap;

import generic.concurrency.Policy;
import tCRDT.map.AddMapOperation;
import tCRDT.map.MapOperation;
import tCRDT.map.RemMapOperation;

public class OptMapAddWinsPolicy implements Policy<MapOperation> {

    public static final String NAME = "mAdd";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(MapOperation op, MapOperation otherOp) {
        return op.getType() == AddMapOperation.ADD && otherOp.getType() == RemMapOperation.REMOVE;
    }

}
