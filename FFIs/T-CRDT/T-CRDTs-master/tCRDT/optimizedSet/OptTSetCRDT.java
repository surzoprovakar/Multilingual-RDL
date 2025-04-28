package tCRDT.optimizedSet;

import generic.models.OptimizedGenericCRDT;
import tCRDT.set.SetOperation;

import java.util.Map;
import java.util.Set;

/**
 * Preferably use optimized policies with this CRDT.
 * Non-optimized ones also work but waste time comparing the elements, which is unnecessary with the optimized model.
 * Do note that optimized policies DO NOT WORK with the non-optimized TSetCRDT.
 */
public class OptTSetCRDT extends AbsOptTSetCRDT {

    public OptTSetCRDT(boolean cache) {
        super(new OptimizedGenericCRDT<>(cache));
    }

    //TODO: Remove
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Set<SetOperation>> getOps() {
        return ((OptimizedGenericCRDT<SetOperation>)(super.getModel())).getOps();
    }
}
