package generic.operations;

import generic.concurrency.History;
import generic.concurrency.Policy;

public interface GenericOperationInterface {

    History getHistory();

    boolean applyHb(GenericOperationInterface otherOp);

    boolean applySelfPolicy(GenericOperationInterface otherOp);

    boolean applyOtherPolicy(GenericOperationInterface otherOp);

    String getHbName();

    String getSelfPolicyName();

    String getOtherPolicyName();

    Policy getHbPolicy();

    Policy getSelfPolicy();

    Policy getOtherPolicy();

    boolean happenedBefore(GenericOperationInterface otherOp);

    boolean isConcurrent(GenericOperationInterface otherOp);
}
