package tCRDT.optimizedMap;

import generic.models.IncrementalOptimizedGenericCRDT;
import tCRDT.map.MapOperation;

import java.util.Map;
import java.util.Set;

/**
 * Preferably use optimized policies with this CRDT.
 * Non-optimized ones also work but waste time comparing the keys, which is unnecessary with the optimized model.
 * Do note that optimized policies DO NOT WORK with the non-optimized TMapCRDT.
 */
public class IncOptTMapCRDT extends AbsOptTMapCRDT {

    public IncOptTMapCRDT(boolean cache) {
        super(new IncrementalOptimizedGenericCRDT<>(cache));
    }

    //TODO: Remove
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Set<MapOperation>> getOps() {
        return ((IncrementalOptimizedGenericCRDT<MapOperation>)(super.getModel())).getOps();
    }
}
