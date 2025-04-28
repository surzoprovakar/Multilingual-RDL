package tCRDT.optimizedSet;

import generic.concurrency.Clock;
import generic.concurrency.History;
import generic.concurrency.Policy;
import generic.models.OptimizedGenericCRDTInterface;
import tCRDT.set.SetOperation;
import tCRDT.set.TSetInterface;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbsOptTSetCRDT implements TSetInterface {

    private OptimizedGenericCRDTInterface<SetOperation> crdt;

    protected AbsOptTSetCRDT(OptimizedGenericCRDTInterface<SetOperation> model) {
        crdt = model;
    }

    public void downstream(SetOperation op) {
        crdt.addOp(op);
    }

    //Note: The same value can be used for both clock and history.
    //The history here is an argument since it is intended to be something externally controlled (such as a VectorClock) instead of the real operation history,
    //as that would be too inefficient.
    public SetOperation add(History hist, Policy<SetOperation> hb, Policy<SetOperation> selfPolicy, Policy<SetOperation> otherPolicy, String element, Clock clk) {
        SetOperation op = new SetOperation(hist, hb, selfPolicy, otherPolicy, SetOperation.ADD, element, clk);
        crdt.addOp(op);
        return op;
    }

    //Note: The same value can be used for both clock and history.
    //The history here is an argument since it is intended to be something externally controlled (such as a VectorClock) instead of the real operation history,
    //as that would be too inefficient.
    public SetOperation remove(History hist, Policy<SetOperation> hb, Policy<SetOperation> selfPolicy, Policy<SetOperation> otherPolicy, String element, Clock clk) {
        SetOperation op = new SetOperation(hist, hb, selfPolicy, otherPolicy, SetOperation.REMOVE, element, clk);
        crdt.addOp(op);
        return op;
    }

    /**
     * <b>Pre-condition</b>: the policies used don't allow an add(element) and remove(element) to be both non-obsolete simultaneously.
     *
     * This lookup is different from the one in the specification, since if it finds a remove(element) it automatically returns false.
     * This means that when used with user-defined policies that allow both add(element) and remove(element) to both stay non-obsolete,
     * this will return different results depending on the order of iteration.
     * @param element - the element to search for
     * @return true, if there's an add(element) that isn't obsolete; false, if there's an remove(element) that isn't obsolete or no add(element) is found.
     */
    public boolean lookup(String element) {
        Set<SetOperation> nonObs = crdt.calculateIdState(element);
        for (SetOperation op: nonObs) {
            if (op.getType() == SetOperation.ADD)
                return true;
            if (op.getType() == SetOperation.REMOVE)
                return false;
        }
        return false;
    }

    /**
     * This lookup corresponds exactly to the one in the specification, i.e., it will ignore any remove(element).
     * @param element - the element to search for
     * @return true, if there's an add(element) that isn't obsolete; false, otherwise.
     */
    public boolean originalLookup(String element) {
        Set<SetOperation> nonObs = crdt.calculateIdState(element);
        for (SetOperation op: nonObs)
            if (op.getType() == SetOperation.ADD)
                return true;
        return false;
    }

    public Set<String> elements() {
        Set<String> result = new HashSet<String>();
        Set<SetOperation> nonObs = crdt.calculateState();
        for (SetOperation op: nonObs)
            if (op.getType() == SetOperation.ADD)
                result.add(op.getElement());
        return result;
    }

    protected OptimizedGenericCRDTInterface getModel() {
        return crdt;
    }

    //TODO: Remove
    public abstract Map<String, Set<SetOperation>> getOps();

    //TODO: Remove
    public Set<SetOperation> getNonObsoleteOps() {
        return crdt.calculateState();
    }
}
