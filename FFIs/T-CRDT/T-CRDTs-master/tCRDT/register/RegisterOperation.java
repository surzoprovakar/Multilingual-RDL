package tCRDT.register;

import generic.concurrency.Clock;
import generic.operations.GenericOperation;
import generic.concurrency.History;
import generic.concurrency.Policy;

public class RegisterOperation extends GenericOperation {

    private String value;
    private Clock clock;

    public RegisterOperation(History hist, Policy<RegisterOperation> hb, Policy<RegisterOperation> selfPolicy,
                        Policy<RegisterOperation> otherPolicy, String value, Clock clock) {
        super(hist, hb, selfPolicy, otherPolicy);
        this.value = value;
        this.clock = clock;
    }

    public String getValue() {
        return value;
    }

    public Clock getClock() {
        return clock;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append("[type: assign");
        string.append(", hbPolicy: ").append(super.getHbName());
        string.append(", selfPolicy: ").append(super.getSelfPolicyName());
        string.append(", otherPolicy: ").append(super.getOtherPolicyName());
        string.append(", value: ").append(value);
        string.append(", clock: ").append(clock.toString());
        return string.toString();
    }

}
