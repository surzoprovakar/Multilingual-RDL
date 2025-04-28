package generic.concurrency;

import generic.operations.GenericOperationInterface;

/**
 * Unlike the name suggests, a class implementing this interface doesn't need to provide a way of knowing the op's
 * whole history. Any implementation that allows to compare the operation this history is associated (op) to with any
 * other operation in order to decide if they are concurrent, op happened-before or op happened-after is correct.
 */
public interface History {

    /**
     * @param otherOp - the operation to check
     * @return true if this operation happened-before otherOp
     */
    boolean isHappensBefore(GenericOperationInterface otherOp);

    /**
     * @param otherOp - the operation to check
     * @return true if the operations are concurrent
     */
    boolean isConcurrent(GenericOperationInterface otherOp);

    /**
     * @param otherOp - the operation to check
     * @return true if this operation happened-after otherOp (or otherOp happened-before this operation)
     */
    boolean isHappensAfter(GenericOperationInterface otherOp);
}
