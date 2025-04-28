package generic.models;

import generic.operations.GenericIdOpInterface;

import java.util.Set;

/**
 *
 * @param <T> - the class/interface corresponding to operations of the "wrapping" t-CRDT. Ex: for TOptSetCRDT, use SetOperation
 */
public interface OptimizedGenericCRDTInterface<T extends GenericIdOpInterface> extends GenericCRDTInterface<T> {

    Set<T> calculateIdState(String id);

}
