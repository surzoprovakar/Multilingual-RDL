package generic.operations;


/**
 * Operation interface which contains an identifier/value in which any two operations with different IDs never interact
 * (i.e., the policies never refer) to each other.
 * Operations that implement this interface can be used in the OptimizedGenericCRDT, which groups operations by their
 * ID, thus turning the procedure calculateState() more efficient (as for each operation it only needs to compare
 * with other operations in the same group instead of all)
 * Examples of CRDTs which meet this criteria: set (element); map (key).
 */
public interface GenericIdOpInterface extends GenericOperationInterface {

    String getId();

}
