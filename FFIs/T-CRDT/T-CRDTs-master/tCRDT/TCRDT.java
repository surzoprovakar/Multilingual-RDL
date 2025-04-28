package tCRDT;

/**
 *
 * @param <T> - the class/interface corresponding to operations of the t-CRDT. Ex: for TSetCRDT, use SetOperation
 */
public interface TCRDT<T> {

    void downstream(T op);

}
