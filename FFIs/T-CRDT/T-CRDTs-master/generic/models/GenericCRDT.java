package generic.models;

import generic.operations.GenericOperationInterface;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @param <T> - the class/interface corresponding to operations of the "wrapping" t-CRDT. Ex: for TSetCRDT, use SetOperation
 */
public class GenericCRDT<T extends GenericOperationInterface> implements GenericCRDTInterface<T> {

    private Set<T> ops;
    //Everytime we receive an operation this gets cleaned.
    private Set<T> cachedNonObs;
    private boolean shouldCache;

    public GenericCRDT(boolean cache) {
        ops = new HashSet<>();
        shouldCache = cache;
        if (shouldCache)
            cachedNonObs = new HashSet<>();
    }

    @Override
    public void addOp(T op) {
        ops.add(op);
        //Can always clean cache, regardless if we're caching or not
        cachedNonObs = null;
    }

    @Override
    public Set<T> calculateState() {
        //No changes since last call.
        if (cachedNonObs != null)
            return cachedNonObs;
        //At least one operation was received, so we need to calculate the new non-obsolete state.
        Set<T> obsByHB = getObsByHB();
        ops.removeAll(obsByHB);
        Set<T> obsByConcurrency = getObsByConcurrency();
        Set<T> result = new HashSet<>();
        for (T op: ops)
            if (!obsByConcurrency.contains(op))
                result.add(op);
        if (shouldCache)
            cachedNonObs = result;
        return result;

    }

    private Set<T> getObsByHB() {
        Set<T> result = new HashSet<>();
        for (T op: ops) {
            for (T otherOp: ops) {
                //opSanity(op, otherOp);
                if (op.happenedBefore(otherOp) && otherOp.applyHb(op)) {
                    result.add(op);
                    break;
                }
            }
        }
        return result;
    }

    private Set<T> getObsByConcurrency() {
        Set<T> result = new HashSet<>();
        for (T op: ops) {
            for (T otherOp: ops) {
                //opSanity(op, otherOp);
                if (op.isConcurrent(otherOp) && (op.applySelfPolicy(otherOp) || otherOp.applyOtherPolicy(op))) {
                    result.add(op);
                    break;
                }
            }
        }
        return result;
    }

    //TODO: Remove
    public Set<T> getOps() {
        return ops;
    }

}
