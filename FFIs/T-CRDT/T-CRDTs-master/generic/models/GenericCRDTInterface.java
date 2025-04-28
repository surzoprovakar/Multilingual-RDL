package generic.models;

import generic.operations.GenericOperationInterface;

import java.util.Set;

/**
 *
 * @param <T> - the class/interface corresponding to operations of the "wrapping" t-CRDT. Ex: for TSetCRDT, use SetOperation
 */
public interface GenericCRDTInterface<T extends GenericOperationInterface> {

    void addOp(T op);

    Set<T> calculateState();
}
