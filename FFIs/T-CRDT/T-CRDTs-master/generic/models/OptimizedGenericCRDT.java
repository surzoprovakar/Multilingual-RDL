package generic.models;

import generic.operations.GenericIdOpInterface;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This model should only be used if the wrapping CRDT's operations implement GenericIdOpInterface.
 * For more details check the GenericIdOpInterface's documentation.
 * @param <T> - the class/interface corresponding to operations of the "wrapping" t-CRDT. Ex: for TOptSetCRDT, use SetOperation
 */
public class OptimizedGenericCRDT<T extends GenericIdOpInterface> implements OptimizedGenericCRDTInterface<T> {

    private Map<String, Set<T>> ops;
    //Everytime we receive an operation this gets cleaned.
    private Map<String, Set<T>> cachedNonObs;
    private boolean shouldCache;

    public OptimizedGenericCRDT(boolean cache) {
        ops = new HashMap<>();
        shouldCache = cache;
        if (shouldCache)
            cachedNonObs = new HashMap<>();
    }

    @Override
    public void addOp(T op) {
        Set<T> idOps = ops.get(op.getId());
        if (idOps == null) {
            idOps = new HashSet<>();
            ops.put(op.getId(), idOps);
        }
        idOps.add(op);
        if (shouldCache)
            cachedNonObs.remove(op.getId());
    }

    @Override
    public Set<T> calculateState() {
        Set<T> result = new HashSet<>();
        for (String id: ops.keySet())
            result.addAll(calculateIdState(id));
        return result;
    }

    public Set<T> calculateIdState(String id) {
        Set<T> idOps;
        if (shouldCache) {
            idOps = cachedNonObs.get(id);
            //No changes since last call.
            if (idOps != null)
                return idOps;
        }

        //At least one operation was received (or we never cached it), so we need to calculate the new non-obsolete state.
        idOps = ops.get(id);
        //Set may be null as we might not have ever received an operation for this id
        if (idOps == null)
            return new HashSet<>();

        Set<T> obsByHB = getObsByHB(idOps);
        idOps.removeAll(obsByHB);
        Set<T> obsByConcurrency = getObsByConcurrency(idOps);
        Set<T> result = new HashSet<>();
        for (T op: idOps)
            if (!obsByConcurrency.contains(op))
                result.add(op);

        if (shouldCache)
            cachedNonObs.put(id, result);
        return result;
    }

    private Set<T> getObsByHB(Set<T> idOps) {
        Set<T> result = new HashSet<>();
        for (T op: idOps) {
            for (T otherOp: idOps) {
                if (op.happenedBefore(otherOp) && otherOp.applyHb(op)) {
                    result.add(op);
                    break;
                }
            }
        }
        return result;
    }

    private Set<T> getObsByConcurrency(Set<T> idOps) {
        Set<T> result = new HashSet<>();
        for (T op: idOps) {
            for (T otherOp: idOps) {
                if (op.isConcurrent(otherOp) && (op.applySelfPolicy(otherOp) || otherOp.applyOtherPolicy(op))) {
                    result.add(op);
                    break;
                }
            }
        }
        return result;
    }

    //TODO: Remove
    public Map<String, Set<T>> getOps() {
        return ops;
    }

}
