package tCRDT.optimizedMap;

import generic.concurrency.Policy;
import tCRDT.map.AddMapOperation;
import tCRDT.map.MapOperation;
import tCRDT.map.RemMapOperation;

public class OptMapRemWinsPolicy implements Policy<MapOperation> {

    public static final String NAME = "mRem";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Boolean apply(MapOperation op, MapOperation otherOp) {
        return op.getType() == RemMapOperation.REMOVE && otherOp.getType() == AddMapOperation.ADD;
    }

}
