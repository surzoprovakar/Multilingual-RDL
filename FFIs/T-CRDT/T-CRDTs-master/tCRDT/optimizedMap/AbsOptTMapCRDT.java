package tCRDT.optimizedMap;

import generic.concurrency.Clock;
import generic.concurrency.History;
import generic.concurrency.Policy;
import generic.models.OptimizedGenericCRDTInterface;
import tCRDT.map.AddMapOperation;
import tCRDT.map.MapOperation;
import tCRDT.map.RemMapOperation;
import tCRDT.map.TMapInterface;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbsOptTMapCRDT implements TMapInterface {

    private OptimizedGenericCRDTInterface<MapOperation> crdt;

    protected AbsOptTMapCRDT(OptimizedGenericCRDTInterface<MapOperation> model) {
        crdt = model;
    }

    public void downstream(MapOperation op) {
        crdt.addOp(op);
    }

    //Note: The same value can be used for both clock and history.
    //The history here is an argument since it is intended to be something externally controlled (such as a VectorClock) instead of the real operation history,
    //as that would be too inefficient.
    public AddMapOperation add(History hist, Policy<MapOperation> hb, Policy<MapOperation> selfPolicy, Policy<MapOperation> otherPolicy, String key, String element, Clock clk) {
        AddMapOperation op = new AddMapOperation(hist, hb, selfPolicy, otherPolicy, key, element, clk);
        crdt.addOp(op);
        return op;
    }

    //Note: The same value can be used for both clock and history.
    //The history here is an argument since it is intended to be something externally controlled (such as a VectorClock) instead of the real operation history,
    //as that would be too inefficient.
    public RemMapOperation remove(History hist, Policy<MapOperation> hb, Policy<MapOperation> selfPolicy, Policy<MapOperation> otherPolicy, String key, Clock clk) {
        RemMapOperation op = new RemMapOperation(hist, hb, selfPolicy, otherPolicy, key, clk);
        crdt.addOp(op);
        return op;
    }

    /**
     * <b>Pre-condition</b>: the policies used don't allow an add(key, value) and remove(key) to be both non-obsolete simultaneously.
     *
     * This contains is different from the one in the specification, since if it finds a remove(key) it automatically returns false.
     * This means that when used with user-defined policies that allow both add(key, value) and remove(key) to both stay non-obsolete,
     * this will return different results depending on the order of iteration.
     * @param key - the key to search for
     * @return true, if there's an add(key) that isn't obsolete; false, if there's an remove(key) that isn't obsolete or no add(key) is found.
     */
    public boolean contains(String key) {
        Set<MapOperation> nonObs = crdt.calculateIdState(key);
        for (MapOperation op: nonObs) {
            if (op.getType() == AddMapOperation.ADD)
                return true;
            if (op.getType() == RemMapOperation.REMOVE)
                return false;
        }
        return false;
    }

    /**
     * This contains corresponds exactly to the one in the specification, i.e., it will ignore any remove(key).
     * @param key - the key to search for
     * @return true, if there's an add(key) that isn't obsolete; false, otherwise.
     */
    public boolean originalContains(String key) {
        Set<MapOperation> nonObs = crdt.calculateIdState(key);
        for (MapOperation op: nonObs)
            if (op.getType() == AddMapOperation.ADD)
                return true;
        return false;
    }

    public Set<String> get(String key) {
        Set<MapOperation> nonObs = crdt.calculateIdState(key);
        Set<String> result = new HashSet<>();
        for (MapOperation op: nonObs)
            if (op.getKey().equals(key) && op.getType() == AddMapOperation.ADD)
                result.add(((AddMapOperation)op).getElement());
        return result;
    }

    public String singleGet(String key) {
        Set<MapOperation> nonObs = crdt.calculateIdState(key);
        for (MapOperation op: nonObs)
            if (op.getKey().equals(key) && op.getType() == AddMapOperation.ADD)
                return ((AddMapOperation)op).getElement();
        return null;
    }

    public Set<String> keys() {
        Set<String> result = new HashSet<>();
        Set<MapOperation> nonObs = crdt.calculateState();
        for (MapOperation op: nonObs)
            if (op.getType() == AddMapOperation.ADD)
                result.add(op.getKey());
        return result;
    }

    public Set<String> elements() {
        Set<String> result = new HashSet<>();
        Set<MapOperation> nonObs = crdt.calculateState();
        for (MapOperation op: nonObs)
            if (op.getType() == AddMapOperation.ADD)
                result.add(((AddMapOperation)op).getElement());
        return result;
    }

    protected OptimizedGenericCRDTInterface getModel() {
        return crdt;
    }

    //TODO: Remove
    public abstract Map<String, Set<MapOperation>> getOps();

    //TODO: Remove
    public Set<MapOperation> getNonObsoleteOps() {
        return crdt.calculateState();
    }


}
