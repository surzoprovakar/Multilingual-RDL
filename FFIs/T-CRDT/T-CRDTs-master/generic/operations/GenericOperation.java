package generic.operations;

import generic.concurrency.History;
import generic.concurrency.Policy;

public class GenericOperation implements GenericOperationInterface {

    private History hist;
    private Policy<GenericOperationInterface> hb;
    private Policy<GenericOperationInterface> selfPolicy;
    private Policy<GenericOperationInterface> otherPolicy;


    @SuppressWarnings("unchecked")
    public GenericOperation(History hist, Policy<? extends GenericOperationInterface> hb,
                            Policy<? extends GenericOperationInterface> selfPolicy,
                            Policy<? extends GenericOperationInterface> otherPolicy) {
        this.hist = hist;
        this.hb = (Policy<GenericOperationInterface>) hb;
        this.selfPolicy = (Policy<GenericOperationInterface>) selfPolicy;
        this.otherPolicy = (Policy<GenericOperationInterface>) otherPolicy;
    }

    public History getHistory() {
        return hist;
    }

    public boolean applyHb(GenericOperationInterface otherOp) {
        return hb.apply(otherOp, this);
    }

    public boolean applySelfPolicy(GenericOperationInterface otherOp) {
        return selfPolicy.apply(otherOp, this);
    }

    public boolean applyOtherPolicy(GenericOperationInterface otherOp) {
        return otherPolicy.apply(this, otherOp);
    }

    @Override
    public String getHbName() {
        return hb.getName();
    }

    @Override
    public String getSelfPolicyName() {
        return selfPolicy.getName();
    }

    @Override
    public String getOtherPolicyName() {
        return otherPolicy.getName();
    }

    @Override
    public Policy getHbPolicy() {
        return hb;
    }

    @Override
    public Policy getSelfPolicy() {
        return selfPolicy;
    }

    @Override
    public Policy getOtherPolicy() {
        return otherPolicy;
    }

    public boolean happenedBefore(GenericOperationInterface otherOp) {
        return hist.isHappensBefore(otherOp);
    }

    public boolean isConcurrent(GenericOperationInterface otherOp) {
        return hist.isConcurrent(otherOp);
    }
}
