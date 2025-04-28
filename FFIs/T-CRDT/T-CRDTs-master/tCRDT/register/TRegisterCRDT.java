package tCRDT.register;

import generic.concurrency.Clock;
import generic.concurrency.History;
import generic.concurrency.Policy;
import generic.models.GenericCRDT;

import java.util.HashSet;
import java.util.Set;

public class TRegisterCRDT implements TRegisterInterface {

    private GenericCRDT<RegisterOperation> crdt;

    public TRegisterCRDT(boolean cache) {
        crdt = new GenericCRDT<>(cache);
    }

    public void downstream(RegisterOperation op) {
        crdt.addOp(op);
    }

    //Note: The same value can be used for both clock and history.
    //The history here is an argument since it is intended to be something externally controlled (such as a VectorClock)
    //instead of the real operation history, as that would be too inefficient.
    public RegisterOperation assign(History hist, Policy<RegisterOperation> hb, Policy<RegisterOperation> selfPolicy, Policy<RegisterOperation> otherPolicy, String value, Clock clk) {
        RegisterOperation op = new RegisterOperation(hist, hb, selfPolicy, otherPolicy, value, clk);
        crdt.addOp(op);
        return op;
    }

    public Set<String> value() {
        Set<RegisterOperation> nonObs = crdt.calculateState();
        Set<String> elements = new HashSet<String>();
        for (RegisterOperation op: nonObs)
            elements.add(op.getValue());
        return elements;
    }

    //TODO: Remove
    public Set<RegisterOperation> getOps() {
        return crdt.getOps();
    }

    //TODO: Remove
    public Set<RegisterOperation> getNonObsoleteOps() {
        return crdt.calculateState();
    }
}
